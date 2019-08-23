package org.uniprot.mavenplugin;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.swagger.v3.core.util.ReflectionUtils;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.apache.commons.io.FileUtils;
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
import org.uniprot.core.*;
import org.uniprot.core.operation.OperationBuilder;
import org.uniprot.core.request.ParameterBuilder;
import org.uniprot.core.request.RequestBodyBuilder;
import org.uniprot.core.request.RequestBuilder;
import org.uniprot.core.response.ResponseBuilder;
import org.uniprot.utils.SpringResource;
import org.uniprot.utils.SpringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;

/**
 * Originally written by https://github.com/springdoc as a part of class AbstractOpenApiResource
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

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            OpenAPI openAPI = getOpenApi();
            String yamlString = Yaml.mapper().writeValueAsString(openAPI);
            // LOGGER.info("The Open API 3.0.1 Spec ");
            //// LOGGER.info(yamlString);
            writeToFile(yamlString);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public OpenAPI getOpenApi() {
        Reflections reflections = new Reflections(this.packageToScan);
        parameterBuilder = new ParameterBuilder();
        requestBodyBuilder = new RequestBodyBuilder(parameterBuilder);
        securityParser = new SecurityParser();
        generalInfoBuilder = new GeneralInfoBuilder(this.serverBaseUrl);
        openAPIBuilder = new OpenAPIBuilder();
        tagsBuilder = new TagsBuilder();
        operationBuilder = new OperationBuilder(parameterBuilder, requestBodyBuilder, securityParser);
        responseBuilder = new ResponseBuilder();
        requestBuilder = new RequestBuilder(parameterBuilder, requestBodyBuilder);

        Instant start = Instant.now();
        generalInfoBuilder.build(openAPIBuilder.getOpenAPI());

        Set<Class<?>> restControllers = reflections.getTypesAnnotatedWith(RestController.class);
        // LOGGER.info("No. of controllers found {}", restControllers.size());
        Set<Class<?>> requestMappingClasses = reflections.getTypesAnnotatedWith(RequestMapping.class);
        Set<Class<?>> allRequiredClasses = new HashSet<>(restControllers);
        allRequiredClasses.addAll(requestMappingClasses);

        // calculate generic responses
        Set<Class<?>> controllerAdvices = reflections.getTypesAnnotatedWith(ControllerAdvice.class);
        responseBuilder.buildGenericResponse(openAPIBuilder.getComponents(), controllerAdvices);
        Map<String, SpringResource> resourceMap = generateResourceMap(restControllers);
        getPaths(resourceMap);
        // LOGGER.info("Init duration for springdoc-openapi is: {} ms", Duration.between(start, Instant.now()).toMillis());
        return openAPIBuilder.getOpenAPI();
    }

    public void getPaths(Map<String, SpringResource> restControllers) {
        for (Map.Entry<String, SpringResource> entry : restControllers.entrySet()) {
            SpringResource sr = entry.getValue();
            String operationPath = sr.getOperationPath();
            Set<RequestMethod> requestMethods = sr.getRequestMethods();
            Method handlerMethod = sr.getMethods().get(0);
            calculatePath(openAPIBuilder, handlerMethod, operationPath, requestMethods);
        }
    }

    protected void calculatePath(OpenAPIBuilder openAPIBuilder, Method handlerMethod, String operationPath,
                                 Set<RequestMethod> requestMethods) {
        OpenAPI openAPI = openAPIBuilder.getOpenAPI();
        Components components = openAPIBuilder.getComponents();
        Paths paths = openAPIBuilder.getPaths();

        for (RequestMethod requestMethod : requestMethods) {
            // skip hidden operations
            io.swagger.v3.oas.annotations.Operation apiOperation = ReflectionUtils
                    .getAnnotation(handlerMethod, io.swagger.v3.oas.annotations.Operation.class);

            boolean hiddenMethod = (ReflectionUtils.getAnnotation(handlerMethod, Hidden.class) != null);

            if (apiOperation != null && (apiOperation.hidden() || hiddenMethod)) {
                continue;
            }

            RequestMapping reqMappingClass = ReflectionUtils.getAnnotation(handlerMethod.getDeclaringClass(),
                    RequestMapping.class);

            MediaAttributes mediaAttributes = new MediaAttributes();
            if (reqMappingClass != null) {
                mediaAttributes.setClassConsumes(reqMappingClass.consumes());
                mediaAttributes.setClassProduces(reqMappingClass.produces());
            }

            mediaAttributes.calculateConsumesProduces(requestMethod, handlerMethod);

            Operation operation = new Operation();

            // specification extension for query param (search query) rules and examples
            Map<String, Object> extensions = getOperationExtensions(handlerMethod);
            operation.setExtensions(extensions);

            // compute tags
            operation = tagsBuilder.build(handlerMethod, operation, openAPI);

            // Add documentation from operation annotation
            if (apiOperation != null) {
                openAPI = operationBuilder.parse(components, apiOperation, operation, openAPI, mediaAttributes);
            }

            // requests
            operation = requestBuilder.build(components, handlerMethod, requestMethod, operation, mediaAttributes);

            // responses
            ApiResponses apiResponses = responseBuilder.build(components, handlerMethod, operation,
                    mediaAttributes.getAllProduces());

            operation.setResponses(apiResponses);

            PathItem pathItemObject = buildPathItem(requestMethod, operation, operationPath, paths);
            paths.addPathItem(operationPath, pathItemObject);
        }
    }

    private Map<String, Object> getOperationExtensions(Method handlerMethod) {
        Map<String, Object> extensions = new LinkedHashMap<>();
        SearchRequestMeta srm = ReflectionUtils.getAnnotation(handlerMethod, SearchRequestMeta.class);
        if (srm != null) {
            String path = srm.path();
            File file = new File(path);
            try {
                String jsonString = FileUtils.readFileToString(file, "UTF-8");
                ObjectMapper objectMapper = new ObjectMapper();
                TypeFactory typeFactory = objectMapper.getTypeFactory();
                List<SearchRow> searchRows = objectMapper.readValue(jsonString, typeFactory.constructCollectionType(List.class, SearchRow.class));
                extensions.put("x-query-param", searchRows);
            } catch (IOException e) {
                LOGGER.warn("Unable to read file {}", file);
            }
        }

        return extensions;
    }

    private PathItem buildPathItem(RequestMethod requestMethod, Operation operation, String operationPath,
                                   Paths paths) {
        PathItem pathItemObject;
        if (paths.containsKey(operationPath)) {
            pathItemObject = paths.get(operationPath);
        } else {
            pathItemObject = new PathItem();
        }
        switch (requestMethod) {
            case POST:
                pathItemObject.post(operation);
                break;
            case GET:
                pathItemObject.get(operation);
                break;
            case DELETE:
                pathItemObject.delete(operation);
                break;
            case PUT:
                pathItemObject.put(operation);
                break;
            case PATCH:
                pathItemObject.patch(operation);
                break;
            case TRACE:
                pathItemObject.trace(operation);
                break;
            case HEAD:
                pathItemObject.head(operation);
                break;
            case OPTIONS:
                pathItemObject.options(operation);
                break;
            default:
                // Do nothing here
                break;
        }
        return pathItemObject;
    }

    protected Map<String, SpringResource> generateResourceMap(Set<Class<?>> validClasses) {
        Map<String, SpringResource> resourceMap = new HashMap<>();
        for (Class<?> aClass : validClasses) {
            resourceMap = analyzeController(aClass, resourceMap, "");
        }

        return resourceMap;
    }

    //FIXME Refactor
    private Map<String, SpringResource> analyzeController(Class<?> controllerClazz, Map<String, SpringResource> resourceMap, String description) {
        String[] controllerRequestMappingValues = SpringUtils.getControllerResquestMapping(controllerClazz);

        // Iterate over all value attributes of the class-level RequestMapping annotation
        for (String controllerRequestMappingValue : controllerRequestMappingValues) {
            for (Method method : controllerClazz.getMethods()) {
                // Skip methods introduced by compiler
                if (method.isSynthetic()) {
                    continue;
                }
                RequestMapping methodRequestMapping = findMergedAnnotation(method, RequestMapping.class);

                // Look for method-level @RequestMapping annotation
                if (methodRequestMapping != null) {
                    RequestMethod[] requestMappingRequestMethods = methodRequestMapping.method();

                    // For each method-level @RequestMapping annotation, iterate over HTTP Verb
                    for (RequestMethod requestMappingRequestMethod : requestMappingRequestMethods) {
                        String[] methodRequestMappingValues = methodRequestMapping.value();

                        // Check for cases where method-level @RequestMapping#value is not set, and use the controllers @RequestMapping
                        if (methodRequestMappingValues.length == 0) {
                            // The map key is a concat of the following:
                            //   1. The controller package
                            //   2. The controller class name
                            //   3. The controller-level @RequestMapping#value
                            String resourceKey = controllerClazz.getCanonicalName() + controllerRequestMappingValue + requestMappingRequestMethod;
                            if (!resourceMap.containsKey(resourceKey)) {
                                SpringResource springResource = new SpringResource(controllerClazz, controllerRequestMappingValue, resourceKey, description);
                                String operationPath = controllerRequestMappingValue;
                                springResource.setOperationPath(operationPath);
                                springResource.addRequestMethod(requestMappingRequestMethod);
                                resourceMap.put(resourceKey, springResource);
                            }
                            resourceMap.get(resourceKey).addMethod(method);
                        } else {
                            // Here we know that method-level @RequestMapping#value is populated, so
                            // iterate over all the @RequestMapping#value attributes, and add them to the resource map.
                            for (String methodRequestMappingValue : methodRequestMappingValues) {
                                String resourceKey = controllerClazz.getCanonicalName() + controllerRequestMappingValue
                                        + methodRequestMappingValue + requestMappingRequestMethod;
                                if (!(controllerRequestMappingValue + methodRequestMappingValue).isEmpty()) {
                                    if (!resourceMap.containsKey(resourceKey)) {
                                        SpringResource springResource = new SpringResource(controllerClazz, methodRequestMappingValue, resourceKey, description);
                                        String operationPath = controllerRequestMappingValue + methodRequestMappingValue;
                                        springResource.setOperationPath(operationPath);
                                        springResource.addRequestMethod(requestMappingRequestMethod);
                                        resourceMap.put(resourceKey, springResource);
                                    }
                                    resourceMap.get(resourceKey).addMethod(method);
                                }
                            }
                        }
                    }
                }
            }
        }

        return resourceMap;
    }

    private void writeToFile(String openApiSpecYaml) throws MojoFailureException {
        File outputDir = new File(this.openApiDirectory);

        if (outputDir.isFile()) {
            throw new MojoFailureException(String.format("OpenAPI-outputDirectory[%s] must be a directory!", this.openApiDirectory));
        }

        if (!outputDir.exists()) {
            try {
                FileUtils.forceMkdir(outputDir);
            } catch (IOException e) {
                throw new MojoFailureException(String.format("Create OpenAPI-outputDirectory[%s] failed.", this.openApiDirectory));
            }
        }

        try {
            FileUtils.write(new File(outputDir, this.openApiFileName), openApiSpecYaml, "UTF-8");
        } catch (IOException e) {
            throw new MojoFailureException("Unable to write the file", e);
        }
    }

    public void setPackageToScan(String packageToScan) {
        this.packageToScan = packageToScan;
    }
}
