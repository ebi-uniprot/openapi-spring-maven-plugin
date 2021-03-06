package uk.ac.ebi.uniprot.openapi.core.operation;

import com.fasterxml.jackson.annotation.JsonView;
import uk.ac.ebi.uniprot.openapi.core.MediaAttributes;
import uk.ac.ebi.uniprot.openapi.core.SecurityParser;
import uk.ac.ebi.uniprot.openapi.core.SpringDocAnnotationsUtils;
import uk.ac.ebi.uniprot.openapi.core.request.ParameterBuilder;
import uk.ac.ebi.uniprot.openapi.core.request.RequestBodyBuilder;
import uk.ac.ebi.uniprot.openapi.utils.Constants;
import io.swagger.v3.core.util.AnnotationsUtils;
import io.swagger.v3.core.util.ReflectionUtils;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.links.Link;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Originally written by https://github.com/springdoc
 *
 * @author Modified by sahmad
 */

public class OperationBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(OperationBuilder.class);

    private ParameterBuilder parameterBuilder;
    private RequestBodyBuilder requestBodyBuilder;
    private SecurityParser securityParser;

    public OperationBuilder(ParameterBuilder parameterBuilder, RequestBodyBuilder requestBodyBuilder, SecurityParser securityParser) {
        this.parameterBuilder = parameterBuilder;
        this.requestBodyBuilder = requestBodyBuilder;
        this.securityParser = securityParser;
    }

    public OpenAPI parse(Components components, io.swagger.v3.oas.annotations.Operation apiOperation,
                         Operation operation, OpenAPI openAPI, MediaAttributes mediaAttributes) {
        if (StringUtils.isNotBlank(apiOperation.summary())) {
            operation.setSummary(apiOperation.summary());
        }
        if (StringUtils.isNotBlank(apiOperation.description())) {
            operation.setDescription(apiOperation.description());
        }
        if (StringUtils.isNotBlank(apiOperation.operationId())) {
            operation.setOperationId(getOperationId(apiOperation.operationId(), openAPI.getPaths()));
        }
        if (apiOperation.deprecated()) {
            operation.setDeprecated(apiOperation.deprecated());
        }

        buildTags(apiOperation, operation);

        if (operation.getExternalDocs() == null) { // if not set in root annotation
            AnnotationsUtils.getExternalDocumentation(apiOperation.externalDocs())
                    .ifPresent(operation::setExternalDocs);
        }

        // servers
        AnnotationsUtils.getServers(apiOperation.servers())
                .ifPresent(servers -> servers.forEach(operation::addServersItem));

        // build parameters
        buildParameters(Arrays.asList(apiOperation.parameters()), operation, components);

        // RequestBody in Operation
        requestBodyBuilder.buildRequestBodyFromDoc(apiOperation.requestBody(), mediaAttributes.getClassConsumes(),
                mediaAttributes.getMethodConsumes(), components, null).ifPresent(operation::setRequestBody);

        // build response if not already set by method level apiresponse annotation
        if(CollectionUtils.isEmpty(operation.getResponses())) {
            buildResponse(components, apiOperation, operation, mediaAttributes);//shadab follow from here
        }

        // security
        Optional<List<SecurityRequirement>> requirementsObject = securityParser
                .getSecurityRequirements(apiOperation.security());
        if (requirementsObject.isPresent()) {
            requirementsObject.get().stream()
                    .filter(r -> operation.getSecurity() == null || !operation.getSecurity().contains(r))
                    .forEach(operation::addSecurityItem);
        }

        // Extensions in Operation
        buildExtensions(apiOperation, operation);
        return openAPI;
    }


    private void buildExtensions(io.swagger.v3.oas.annotations.Operation apiOperation, Operation operation) {
        if (apiOperation.extensions().length > 0) {
            Map<String, Object> extensions = AnnotationsUtils.getExtensions(apiOperation.extensions());
            for (Map.Entry<String, Object> entry : extensions.entrySet()) {
                operation.addExtension(entry.getKey(), entry.getValue());
            }
        }
    }

    private void buildTags(io.swagger.v3.oas.annotations.Operation apiOperation, Operation operation) {
        Optional<List<String>> mlist = getStringListFromStringArray(apiOperation.tags());
        if (mlist.isPresent()) {
            List<String> tags = mlist.get().stream()
                    .filter(t -> operation.getTags() == null
                            || (operation.getTags() != null && !operation.getTags().contains(t)))
                    .collect(Collectors.toList());
            for (String tagsItem : tags) {
                operation.addTagsItem(tagsItem);
            }
        }
    }

    public String getOperationId(String operationId, Paths paths) {
        boolean operationIdUsed = existOperationId(operationId, paths);
        String operationIdToFind = null;
        int counter = 0;
        while (operationIdUsed) {
            operationIdToFind = String.format("%s_%d", operationId, ++counter);
            operationIdUsed = existOperationId(operationIdToFind, paths);
        }
        if (operationIdToFind != null) {
            operationId = operationIdToFind;
        }
        return operationId;
    }

    private boolean existOperationId(String operationId, Paths paths) {
        if (paths == null || paths.isEmpty()) {
            return false;
        }
        for (PathItem path : paths.values()) {
            Set<String> pathOperationIds = extractOperationIdFromPathItem(path);
            if (pathOperationIds.contains(operationId)) {
                return true;
            }
        }
        return false;
    }

    private Set<String> extractOperationIdFromPathItem(PathItem path) {
        Set<String> ids = new HashSet<>();
        if (path.getGet() != null && StringUtils.isNotBlank(path.getGet().getOperationId())) {
            ids.add(path.getGet().getOperationId());
        }
        if (path.getPost() != null && StringUtils.isNotBlank(path.getPost().getOperationId())) {
            ids.add(path.getPost().getOperationId());
        }
        if (path.getPut() != null && StringUtils.isNotBlank(path.getPut().getOperationId())) {
            ids.add(path.getPut().getOperationId());
        }
        if (path.getDelete() != null && StringUtils.isNotBlank(path.getDelete().getOperationId())) {
            ids.add(path.getDelete().getOperationId());
        }
        if (path.getOptions() != null && StringUtils.isNotBlank(path.getOptions().getOperationId())) {
            ids.add(path.getOptions().getOperationId());
        }
        if (path.getHead() != null && StringUtils.isNotBlank(path.getHead().getOperationId())) {
            ids.add(path.getHead().getOperationId());
        }
        if (path.getPatch() != null && StringUtils.isNotBlank(path.getPatch().getOperationId())) {
            ids.add(path.getPatch().getOperationId());
        }
        return ids;
    }

    private Optional<ApiResponses> getApiResponses(
            final List<io.swagger.v3.oas.annotations.responses.ApiResponse> responses, String[] classProduces,
            String[] methodProduces, Components components, JsonView jsonViewAnnotation) {

        ApiResponses apiResponsesObject = new ApiResponses();
        for (io.swagger.v3.oas.annotations.responses.ApiResponse response : responses) {
            ApiResponse apiResponseObject = new ApiResponse();
            if (StringUtils.isNotBlank(response.ref())) {
                setRef(apiResponsesObject, response, apiResponseObject);
                continue;
            }
            setDescription(response, apiResponseObject);
            setExtensions(response, apiResponseObject);

            SpringDocAnnotationsUtils.getContent(response.content(),
                    classProduces == null ? new String[0] : classProduces,
                    methodProduces == null ? new String[0] : methodProduces, null, components, jsonViewAnnotation)
                    .ifPresent(apiResponseObject::content);
            AnnotationsUtils.getHeaders(response.headers(), jsonViewAnnotation).ifPresent(apiResponseObject::headers);
            // Make schema as string if empty
            calculateHeader(apiResponseObject);
            if (isResponseObject(apiResponseObject)) {
                setLinks(response, apiResponseObject);
                if (StringUtils.isNotBlank(response.responseCode())) {
                    apiResponsesObject.addApiResponse(response.responseCode(), apiResponseObject);
                } else {
                    apiResponsesObject._default(apiResponseObject);
                }
            }
        }

        return Optional.of(apiResponsesObject);
    }

    private boolean isResponseObject(ApiResponse apiResponseObject) {
        return StringUtils.isNotBlank(apiResponseObject.getDescription()) || apiResponseObject.getContent() != null
                || apiResponseObject.getHeaders() != null;
    }

    private void setLinks(io.swagger.v3.oas.annotations.responses.ApiResponse response, ApiResponse apiResponseObject) {
        Map<String, Link> links = AnnotationsUtils.getLinks(response.links());
        if (links.size() > 0) {
            apiResponseObject.setLinks(links);
        }
    }

    private void setDescription(io.swagger.v3.oas.annotations.responses.ApiResponse response,
                                ApiResponse apiResponseObject) {
        if (StringUtils.isNotBlank(response.description())) {
            apiResponseObject.setDescription(response.description());
        } else {
            apiResponseObject.setDescription(Constants.DEFAULT_DESCRIPTION);
        }
    }

    private void calculateHeader(ApiResponse apiResponseObject) {
        Map<String, Header> headers = apiResponseObject.getHeaders();
        if (!CollectionUtils.isEmpty(headers)) {
            for (Map.Entry<String, Header> entry : headers.entrySet()) {
                Header header = entry.getValue();
                if (header.getSchema() == null) {
                    Schema<?> schema = AnnotationsUtils.resolveSchemaFromType(String.class, null, null);
                    header.setSchema(schema);
                    entry.setValue(header);
                }
            }
        }
    }

    private void setRef(ApiResponses apiResponsesObject, io.swagger.v3.oas.annotations.responses.ApiResponse response,
                        ApiResponse apiResponseObject) {
        apiResponseObject.set$ref(response.ref());
        if (StringUtils.isNotBlank(response.responseCode())) {
            apiResponsesObject.addApiResponse(response.responseCode(), apiResponseObject);
        } else {
            apiResponsesObject._default(apiResponseObject);
        }
    }

    private void setExtensions(io.swagger.v3.oas.annotations.responses.ApiResponse response,
                               ApiResponse apiResponseObject) {
        if (response.extensions().length > 0) {
            Map<String, Object> extensions = AnnotationsUtils.getExtensions(response.extensions());
            for (Map.Entry<String, Object> entry : extensions.entrySet()) {
                apiResponseObject.addExtension(entry.getKey(), entry.getValue());
            }
        }
    }

    private void buildResponse(Components components, io.swagger.v3.oas.annotations.Operation apiOperation,
                               Operation operation, MediaAttributes mediaAttributes) {

        setApiResponses(components, Arrays.asList(apiOperation.responses()), operation, mediaAttributes);
    }

    public void setApiResponses(Components components, List<io.swagger.v3.oas.annotations.responses.ApiResponse> apiResponses,
                                 Operation operation, MediaAttributes mediaAttributes) {

        getApiResponses(apiResponses, mediaAttributes.getClassProduces(),
                mediaAttributes.getMethodProduces(), components, null).ifPresent(responses -> {
            if (operation.getResponses() == null) {
                operation.setResponses(responses);
            } else {
                responses.forEach(operation.getResponses()::addApiResponse);
            }
        });
    }

    private Optional<List<String>> getStringListFromStringArray(String[] array) {
        if (array == null) {
            return Optional.empty();
        }
        List<String> list = new ArrayList<>();
        boolean isEmpty = true;
        for (String value : array) {
            if (StringUtils.isNotBlank(value)) {
                isEmpty = false;
            }
            list.add(value);
        }
        if (isEmpty) {
            return Optional.empty();
        }
        return Optional.of(list);
    }



    public void setParametersMethodLevel(Method handlerMethod, Operation operation, Components components) {
        // get the repeatable parameters outside operation tag
        List<io.swagger.v3.oas.annotations.Parameter> parameters = ReflectionUtils.getRepeatableAnnotations(handlerMethod,
                io.swagger.v3.oas.annotations.Parameter.class);
        buildParameters(parameters, operation, components);
    }

    private void buildParameters(List<io.swagger.v3.oas.annotations.Parameter> parameters, Operation operation, Components components) {
        for (io.swagger.v3.oas.annotations.Parameter parameterDoc : parameters) {
            Parameter parameter = parameterBuilder.buildParameterFromDoc(parameterDoc, components);
            operation.addParametersItem(parameter);
        }
    }

    public void setApiResponseMethodLevel(Method handlerMethod, Operation operation, Components components, MediaAttributes mediaAttributes) {
        // get the repeatable ApiResponse on method level
        List<io.swagger.v3.oas.annotations.responses.ApiResponse> apiResponses = ReflectionUtils.getRepeatableAnnotations
                (handlerMethod, io.swagger.v3.oas.annotations.responses.ApiResponse.class);

        setApiResponses(components, apiResponses, operation, mediaAttributes);
    }
}
