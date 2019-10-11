package uk.ac.ebi.uniprot.openapi.mavenplugin;


import com.fasterxml.jackson.core.JsonProcessingException;
import uk.ac.ebi.uniprot.openapi.FileHelper;
import uk.ac.ebi.uniprot.openapi.PathItemBuilder;
import uk.ac.ebi.uniprot.openapi.core.*;
import uk.ac.ebi.uniprot.openapi.core.operation.OperationBuilder;
import uk.ac.ebi.uniprot.openapi.core.request.ModelFieldParameterBuilder;
import uk.ac.ebi.uniprot.openapi.core.request.ParameterBuilder;
import uk.ac.ebi.uniprot.openapi.core.request.RequestBodyBuilder;
import uk.ac.ebi.uniprot.openapi.core.request.RequestBuilder;
import uk.ac.ebi.uniprot.openapi.core.response.ResponseBuilder;
import uk.ac.ebi.uniprot.openapi.utils.SpringControllerMethod;
import io.swagger.v3.core.util.ReflectionUtils;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * Originally written by https://github.com/springdoc as a part of class AbstractOpenApiResource
 *
 * @author Modified by sahmad to make it non-spring project
 */

@Mojo(name = "oas-generate", defaultPhase = LifecyclePhase.COMPILE, configurator = "include-project-dependencies",
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, threadSafe = true)
public class OpenAPIMojo extends AbstractMojo {

    @Parameter(required = true)
    private List<String> packageLocations; // list packageLocations to scan where controllers and related classes are

    @Parameter(defaultValue = "localhost")
    private String serverBaseUrl;

    @Parameter(defaultValue = "target/generated-sources/swagger/")
    private String openApiDirectory;

    @Parameter(defaultValue = "openapi3.yaml")
    private String openApiFileName;


    protected static final Logger LOGGER = LoggerFactory.getLogger(OpenAPIMojo.class);
    private OpenAPIBuilder openAPIBuilder;
    private TagsBuilder tagsBuilder;
    private OperationBuilder operationBuilder;
    private ParameterBuilder parameterBuilder;
    private RequestBodyBuilder requestBodyBuilder;
    private SecurityParser securityParser;
    private GeneralInfoBuilder generalInfoBuilder;
    private RequestBuilder requestBuilder;
    private ResponseBuilder responseBuilder;
    private PathItemBuilder pathItemBuilder;
    private ModelFieldParameterBuilder modelAttribParamBuilder;

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            OpenAPI openAPI = getOpenApi();
            String yamlString = Yaml.mapper().writeValueAsString(openAPI);
            FileHelper.writeToFile(yamlString, this.openApiDirectory, this.openApiFileName);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public OpenAPI getOpenApi() {
        Instant start = Instant.now();
        // init the builders
        initialise();

        // default values for info tags and default server.. TODO make it configurable
        generalInfoBuilder.build(openAPIBuilder.getOpenAPI());
        Set<Class<?>> allRequiredClasses = new HashSet<>();
        Set<Class<?>> allControllerAdvices = new HashSet<>();

        for(String location : this.packageLocations) {
            Reflections reflections = new Reflections(location);
            // read all the classes which can have REST APIs
            Set<Class<?>> restControllers = reflections.getTypesAnnotatedWith(RestController.class);
            Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(Controller.class);
            Set<Class<?>> requestMappingClasses = reflections.getTypesAnnotatedWith(RequestMapping.class);

            allRequiredClasses.addAll(restControllers);
            allRequiredClasses.addAll(requestMappingClasses);
            allRequiredClasses.addAll(controllers);
            // calculate generic responses TODO write a test case for controller advice
            Set<Class<?>> controllerAdvices = reflections.getTypesAnnotatedWith(ControllerAdvice.class);
            allControllerAdvices.addAll(controllerAdvices);
        }

        responseBuilder.buildGenericResponse(openAPIBuilder.getComponents(), allControllerAdvices);
        // get the method --> SpringMethod map
        Map<String, SpringControllerMethod> resourceMap = generateResourceMap(allRequiredClasses);
        populateControllerPaths(resourceMap);
        LOGGER.info("Time taken to generate openapi doc is: {} ms", Duration.between(start, Instant.now()).toMillis());

        return openAPIBuilder.getOpenAPI();
    }

    // init the builders
    private void initialise() {
        parameterBuilder = new ParameterBuilder();
        requestBodyBuilder = new RequestBodyBuilder(parameterBuilder);
        securityParser = new SecurityParser();
        generalInfoBuilder = new GeneralInfoBuilder(this.serverBaseUrl);
        openAPIBuilder = new OpenAPIBuilder();
        tagsBuilder = new TagsBuilder();
        operationBuilder = new OperationBuilder(parameterBuilder, requestBodyBuilder, securityParser);
        responseBuilder = new ResponseBuilder();
        modelAttribParamBuilder = new ModelFieldParameterBuilder(parameterBuilder);
        requestBuilder = new RequestBuilder(parameterBuilder, requestBodyBuilder, modelAttribParamBuilder);
        pathItemBuilder = new PathItemBuilder(operationBuilder);
    }

    private Map<String, SpringControllerMethod> generateResourceMap(Set<Class<?>> validClasses) {
        Map<String, SpringControllerMethod> resourceMap = new HashMap<>();
        ControllerAnalyser analyser = new ControllerAnalyser();

        for (Class<?> aClass : validClasses) {
            resourceMap = analyser.analyseController(aClass, resourceMap);
        }

        return resourceMap;
    }

    private void populateControllerPaths(Map<String, SpringControllerMethod> resourceMap) {
        for (Map.Entry<String, SpringControllerMethod> entry : resourceMap.entrySet()) {
            SpringControllerMethod springControllerMethod = entry.getValue();
            String operationPath = springControllerMethod.getOperationPath();
            RequestMethod requestMethod = springControllerMethod.getRequestMethod();
            Method handlerMethod = springControllerMethod.getMethod();
            populatePath(handlerMethod, operationPath, requestMethod);
        }
    }

    private void populatePath(Method handlerMethod, String operationPath, RequestMethod requestMethod) {

        // skip hidden operations
        io.swagger.v3.oas.annotations.Operation apiOperation = ReflectionUtils
                .getAnnotation(handlerMethod, io.swagger.v3.oas.annotations.Operation.class);

        boolean hiddenMethod = (ReflectionUtils.getAnnotation(handlerMethod, Hidden.class) != null);

        if (apiOperation != null && (apiOperation.hidden() || hiddenMethod)) {
            return;
        }

        RequestMapping reqMappingClass = ReflectionUtils.getAnnotation(handlerMethod.getDeclaringClass(), RequestMapping.class);

        MediaAttributes mediaAttributes = new MediaAttributes();
        if (reqMappingClass != null) {
            mediaAttributes.setClassConsumes(reqMappingClass.consumes());
            mediaAttributes.setClassProduces(reqMappingClass.produces());
        }

        mediaAttributes.calculateConsumesProduces(requestMethod, handlerMethod);

        Operation operation = new Operation();

        OpenAPI openAPI = openAPIBuilder.getOpenAPI();
        // compute tags
        operation = tagsBuilder.build(handlerMethod, operation, openAPI);

        Components components = openAPIBuilder.getComponents();

        // add repeatable @Parameter annotation see test case in RepeatableParamertersResource.java
        operationBuilder.setParametersMethodLevel(handlerMethod, operation, components);

        // Add documentation from operation annotation
        if (apiOperation != null) {
            operationBuilder.parse(components, apiOperation, operation, openAPI, mediaAttributes);
        }

        // requests with params
        operation = requestBuilder.build(components, handlerMethod, requestMethod, operation, mediaAttributes);

        // responses
        ApiResponses apiResponses = responseBuilder.build(components, handlerMethod, operation,
                mediaAttributes.getAllProduces());

        operation.setResponses(apiResponses);

        Paths paths = openAPIBuilder.getPaths();
        PathItem pathItemObject = this.pathItemBuilder.buildPathItem(requestMethod, operation, operationPath, paths);
        paths.addPathItem(operationPath, pathItemObject);
    }

    // Do not delete this method. For testing
    public void setPackageToScan(List<String> packageLocations) {
        this.packageLocations = packageLocations;
    }

    // Do not delete this method. For testing
    public void setOpenApiFileName(String openApiFileName){
        this.openApiFileName = openApiFileName;
    }
}
