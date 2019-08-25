package org.uniprot.mavenplugin;


import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.uniprot.FileHelper;
import org.uniprot.PathItemBuilder;
import org.uniprot.core.*;
import org.uniprot.core.operation.OperationBuilder;
import org.uniprot.core.request.ParameterBuilder;
import org.uniprot.core.request.RequestBodyBuilder;
import org.uniprot.core.request.RequestBuilder;
import org.uniprot.core.response.ResponseBuilder;
import org.uniprot.utils.SpringControllerMethod;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * Originally written by https://github.com/springdoc as a part of class AbstractOpenApiResource
 *
 * @author Modified by sahmad to make it non-spring project
 */

//TODO see other attributes in Mojo annotation
@Mojo(name = "generate", defaultPhase = LifecyclePhase.COMPILE, configurator = "include-project-dependencies",
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, threadSafe = true)
public class OpenAPIMojo extends AbstractMojo {

    @Parameter(required = true)
    private String packageToScan; // package to scan where controllers and related classes are

    @Parameter(defaultValue = "localhost")
    private String serverBaseUrl;

    @Parameter(required = true)
    private String openApiDirectory;

    @Parameter(defaultValue = "openapi.yaml")
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

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            OpenAPI openAPI = getOpenApi();
            String yamlString = Yaml.mapper().writeValueAsString(openAPI);
            // LOGGER.info("The Open API 3.0.1 Spec ");
            //// LOGGER.info(yamlString);
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

        Reflections reflections = new Reflections(this.packageToScan);
        // TODO think about other types of annotation on the controller classes.
        Set<Class<?>> restControllers = reflections.getTypesAnnotatedWith(RestController.class);
        // LOGGER.info("No. of controllers found {}", restControllers.size());
        Set<Class<?>> requestMappingClasses = reflections.getTypesAnnotatedWith(RequestMapping.class);
        Set<Class<?>> allRequiredClasses = new HashSet<>();
        allRequiredClasses.addAll(restControllers);
        allRequiredClasses.addAll(requestMappingClasses);

        // calculate generic responses TODO write a test case for controller adbvice
        Set<Class<?>> controllerAdvices = reflections.getTypesAnnotatedWith(ControllerAdvice.class);
        responseBuilder.buildGenericResponse(openAPIBuilder.getComponents(), controllerAdvices);

        //TODO test it. earlier io was passing rest contorller now, i am passing the all Required Classes
        Map<String, SpringControllerMethod> resourceMap = generateResourceMap(allRequiredClasses);

        populateControllerPaths(resourceMap);

        LOGGER.info("Time taken to generate openapi doc is: {} ms", Duration.between(start, Instant.now()).toMillis());

        return openAPIBuilder.getOpenAPI();
    }

    // Do not delete this method. Can be used for testing
    public void setPackageToScan(String packageToScan) {
        this.packageToScan = packageToScan;
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
        requestBuilder = new RequestBuilder(parameterBuilder, requestBodyBuilder);
        pathItemBuilder = new PathItemBuilder();
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

        // specification extension for query param (search query) rules and examples
        // set operation specification extension
        operationBuilder.setCustomOperationExt(operation, handlerMethod);

        OpenAPI openAPI = openAPIBuilder.getOpenAPI();
        // compute tags
        operation = tagsBuilder.build(handlerMethod, operation, openAPI);

        Components components = openAPIBuilder.getComponents();
        // Add documentation from operation annotation
        if (apiOperation != null) {
            operationBuilder.parse(components, apiOperation, operation, openAPI, mediaAttributes);
        }

        // requests
        operation = requestBuilder.build(components, handlerMethod, requestMethod, operation, mediaAttributes);

        // responses
        ApiResponses apiResponses = responseBuilder.build(components, handlerMethod, operation,
                mediaAttributes.getAllProduces());

        operation.setResponses(apiResponses);

        Paths paths = openAPIBuilder.getPaths();
        PathItem pathItemObject = this.pathItemBuilder.buildPathItem(requestMethod, operation, operationPath, paths);
        paths.addPathItem(operationPath, pathItemObject);
    }



}