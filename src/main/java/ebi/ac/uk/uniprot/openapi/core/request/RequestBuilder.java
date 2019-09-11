package ebi.ac.uk.uniprot.openapi.core.request;

import ebi.ac.uk.uniprot.openapi.utils.Constants;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;
import ebi.ac.uk.uniprot.openapi.core.MediaAttributes;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Originally written by https://github.com/springdoc
 * @author Modified by sahmad to make it non-spring project
 */

public class RequestBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestBuilder.class);
    private final ModelFieldParameterBuilder modelAttribBuilder;

    private ParameterBuilder parameterBuilder;

    private RequestBodyBuilder requestBodyBuilder;

    public RequestBuilder(ParameterBuilder parameterBuilder, RequestBodyBuilder requestBodyBuilder, ModelFieldParameterBuilder modelFieldParameterBuilder) {
        this.parameterBuilder = parameterBuilder;
        this.requestBodyBuilder = requestBodyBuilder;
        this.modelAttribBuilder = modelFieldParameterBuilder;
    }

    boolean isParamTypeToIgnore(Class<?> paramType) {
		return WebRequest.class.equals(paramType) || NativeWebRequest.class.equals(paramType)
				|| javax.servlet.ServletRequest.class.isAssignableFrom(paramType)
				|| javax.servlet.ServletResponse.class.isAssignableFrom(paramType)
				|| javax.servlet.http.HttpSession.class.equals(paramType)
				|| java.security.Principal.class.equals(paramType) || HttpMethod.class.equals(paramType)
				|| java.util.Locale.class.equals(paramType) || java.util.TimeZone.class.equals(paramType)
				|| java.time.ZoneId.class.equals(paramType) || java.io.InputStream.class.equals(paramType)
				|| java.io.Reader.class.equals(paramType) || java.io.OutputStream.class.equals(paramType)
				|| java.io.Writer.class.equals(paramType) || java.util.Map.class.equals(paramType)
				|| org.springframework.ui.Model.class.equals(paramType)
				|| org.springframework.ui.ModelMap.class.equals(paramType) || RedirectAttributes.class.equals(paramType)
				|| Errors.class.equals(paramType) || BindingResult.class.equals(paramType)
				|| SessionStatus.class.equals(paramType) || UriComponentsBuilder.class.equals(paramType)
                || SimpleQuery.class.equals(paramType);
    }

    public Operation build(Components components, Method handlerMethod, RequestMethod requestMethod,
                           Operation operation, MediaAttributes mediaAttributes) {
        // Documentation
        operation.setOperationId(handlerMethod.getName());
        // requests
        LocalVariableTableParameterNameDiscoverer d = new LocalVariableTableParameterNameDiscoverer();
        String[] pNames = d.getParameterNames(handlerMethod);

        List<Parameter> operationParameters = CollectionUtils.isEmpty(operation.getParameters()) ?
                new ArrayList<>() : operation.getParameters();

        java.lang.reflect.Parameter[] parameters = handlerMethod.getParameters();

        for (int i = 0; i < pNames.length; i++) {
            // check if query param
            Parameter parameter = null;
            Class<?> paramType = parameters[i].getType();
            io.swagger.v3.oas.annotations.Parameter parameterDoc = parameterBuilder.getParameterAnnotation(
                    handlerMethod, parameters[i], i, io.swagger.v3.oas.annotations.Parameter.class);

            ModelAttribute modelAttributeAnnot = AnnotationUtils.getAnnotation(parameters[i], ModelAttribute.class);

            // deal with ModelAttribute annotation
            if (parameterDoc == null
                    && modelAttribBuilder.isValidModelAttribute(parameters[i].getParameterizedType(), modelAttributeAnnot)) {//@Parameter has higher precedence than @ModelAttribute

                List<Parameter> params = modelAttribBuilder.buildParametersFromModelAttribute(parameters[i]);
                operationParameters.addAll(params);

            } else {
                // use documentation as reference
                if (parameterDoc != null) {
                    if (parameterDoc.hidden()) {
                        continue;
                    }
                    parameter = parameterBuilder.buildParameterFromDoc(parameterDoc, null);
                }

                if (!isParamTypeToIgnore(paramType)) {
                    parameter = buildParams(pNames[i], components, parameters[i], i, parameter, handlerMethod, requestMethod);
                    if (parameter != null && parameter.getName() != null) {
                        parameterBuilder.applyBeanValidatorAnnotations(parameter, Arrays.asList(parameters[i].getAnnotations()));
                        operationParameters.add(parameter);
                    } else if (!RequestMethod.GET.equals(requestMethod)) {
                        RequestBody requestBody = requestBodyBuilder.calculateRequestBody(components, handlerMethod,
                                mediaAttributes, pNames, parameters, i, parameterDoc);
                        operation.setRequestBody(requestBody);
                    }
                }
            }
        }

        if(!CollectionUtils.isEmpty(operationParameters)){
            operation.setParameters(operationParameters);
        }

        return operation;
    }

    private Parameter buildParams(String pName, Components components, java.lang.reflect.Parameter parameters,
                                  int index, Parameter parameter, Method handlerMethod, RequestMethod requestMethod) {

        RequestHeader requestHeader = parameterBuilder.getParameterAnnotation(handlerMethod, parameters, index,
                RequestHeader.class);
        RequestParam requestParam = parameterBuilder.getParameterAnnotation(handlerMethod, parameters, index,
                RequestParam.class);
        PathVariable pathVar = parameterBuilder.getParameterAnnotation(handlerMethod, parameters, index,
                PathVariable.class);
        CookieValue cookieValue = parameterBuilder.getParameterAnnotation(handlerMethod, parameters, index,
                CookieValue.class);

        //FIXME use switch statement
        if (requestHeader != null) {
            String name = StringUtils.isBlank(requestHeader.value()) ? pName : requestHeader.value();
            parameter = this.buildParam(Constants.HEADER_PARAM, components, parameters, requestHeader.required(), name,
                    parameter);
        } else if (requestParam != null) {
            String name = StringUtils.isBlank(requestParam.value()) ? pName : requestParam.value();
            parameter = this.buildParam(Constants.QUERY_PARAM, components, parameters, requestParam.required(), name, parameter);
        } else if (pathVar != null) {
            String name = StringUtils.isBlank(pathVar.value()) ? pName : pathVar.value();
            // check if PATH PARAM
            parameter = this.buildParam(Constants.PATH_PARAM, components, parameters, Boolean.TRUE, name, parameter);
        } else if(cookieValue != null){
            String name = StringUtils.isBlank(cookieValue.value()) ? pName : cookieValue.value();
            parameter = this.buildParam(Constants.COOKIE_PARAM, components, parameters, Boolean.TRUE, name, parameter);
        }
        // By default
        if (RequestMethod.GET.equals(requestMethod)) {
            if (parameter == null) {
                parameter = this.buildParam(Constants.QUERY_PARAM, null, parameters, Boolean.TRUE, pName, null);
            } else if (parameter.getName() == null) {
                parameter.setName(pName);
            }
        }
        return parameter;
    }

    private Parameter buildParam(String in, Components components, java.lang.reflect.Parameter parameters,
                                 Boolean required, String name, Parameter parameter) {
        if (parameter == null) {
            parameter = new Parameter();
        }
        parameter.setIn(in);
        parameter.setRequired(required);
        parameter.setName(name);
        Schema<?> schema = parameterBuilder.calculateSchema(components, parameters, name);
        parameter.setSchema(schema);
        return parameter;
    }
}
