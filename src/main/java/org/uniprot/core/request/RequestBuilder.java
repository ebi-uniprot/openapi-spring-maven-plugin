package org.uniprot.core.request;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
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
import org.uniprot.core.MediaAttributes;

import javax.validation.constraints.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

import static org.uniprot.utils.Constants.*;

/**
 * Originally written by https://github.com/springdoc
 * @author Modified by sahmad to make it non-spring project
 */

public class RequestBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestBuilder.class);

    private ParameterBuilder parameterBuilder;

    private RequestBodyBuilder requestBodyBuilder;

    public RequestBuilder(ParameterBuilder parameterBuilder, RequestBodyBuilder requestBodyBuilder) {
        this.parameterBuilder = parameterBuilder;
        this.requestBodyBuilder = requestBodyBuilder;
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
        List<Parameter> operationParameters = new ArrayList<>();
        java.lang.reflect.Parameter[] parameters = handlerMethod.getParameters();

        for (int i = 0; i < pNames.length; i++) {
            // check if query param
            Parameter parameter = null;
            Class<?> paramType = parameters[i].getType();
            // LOGGER.info("paramType {}", paramType);
            io.swagger.v3.oas.annotations.Parameter parameterDoc = parameterBuilder.getParameterAnnotation(
                    handlerMethod, parameters[i], i, io.swagger.v3.oas.annotations.Parameter.class);
            // LOGGER.info("Is paramdoc null {}", parameterDoc);

            // use documentation as reference
            if (parameterDoc != null) {
                if (parameterDoc.hidden()) {
                    continue;
                }
                // LOGGER.info("In here without compoenents");
                parameter = parameterBuilder.buildParameterFromDoc(parameterDoc, null);
            }

            if (!isParamTypeToIgnore(paramType)) {
                // LOGGER.info("calling for {}", pNames[i]);
                parameter = buildParams(pNames[i], components, parameters[i], i, parameter, handlerMethod, requestMethod);
                // LOGGER.info("parameter {}", parameter);
                if (parameter != null && parameter.getName() != null) {
                    applyBeanValidatorAnnotations(parameter, Arrays.asList(parameters[i].getAnnotations()));
                    operationParameters.add(parameter);
                } else if (!RequestMethod.GET.equals(requestMethod)) {
                    RequestBody requestBody = requestBodyBuilder.calculateRequestBody(components, handlerMethod,
                            mediaAttributes, pNames, parameters, i, parameterDoc);
                    operation.setRequestBody(requestBody);
                }
            }
        }
        if (!CollectionUtils.isEmpty(operationParameters)) {
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
            parameter = this.buildParam(HEADER_PARAM, components, parameters, requestHeader.required(), name,
                    parameter);
        } else if (requestParam != null) {
            // LOGGER.info("PNAME IS ================> {}", pName);
            String name = StringUtils.isBlank(requestParam.value()) ? pName : requestParam.value();
            parameter = this.buildParam(QUERY_PARAM, components, parameters, requestParam.required(), name, parameter);
        } else if (pathVar != null) {
            String name = StringUtils.isBlank(pathVar.value()) ? pName : pathVar.value();
            // check if PATH PARAM
            parameter = this.buildParam(PATH_PARAM, components, parameters, Boolean.TRUE, name, parameter);
        } else if(cookieValue != null){
            String name = StringUtils.isBlank(cookieValue.value()) ? pName : cookieValue.value();
            parameter = this.buildParam(COOKIE_PARAM, components, parameters, Boolean.TRUE, name, parameter);
        }
        // By default
        if (RequestMethod.GET.equals(requestMethod)) {
            if (parameter == null) { // TODO added components in param to create schema of Get Param of type object e.g. KeywordSearchDTO
                parameter = this.buildParam(QUERY_PARAM, null, parameters, Boolean.TRUE, pName, null);
//                parameter = this.buildParam(QUERY_PARAM, components, parameters, Boolean.TRUE, pName, null);
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

    /**
     * This is mostly a duplicate of
     * io.swagger.v3.core.jackson.ModelResolver#applyBeanValidatorAnnotations}.
     *
     * @param parameter
     * @param annotations
     */
    private void applyBeanValidatorAnnotations(final Parameter parameter, final List<Annotation> annotations) {
        Map<String, Annotation> annos = new HashMap<>();
        if (annotations != null) {
            annotations.forEach(annotation -> annos.put(annotation.annotationType().getName(), annotation));
        }

        if (annos.containsKey(NotNull.class.getName())) {
            parameter.setRequired(true);
        }

        Schema<?> schema = parameter.getSchema();

        if (annos.containsKey(Min.class.getName())) {
            Min min = (Min) annos.get(Min.class.getName());
            schema.setMinimum(BigDecimal.valueOf(min.value()));
        }
        if (annos.containsKey(Max.class.getName())) {
            Max max = (Max) annos.get(Max.class.getName());
            schema.setMaximum(BigDecimal.valueOf(max.value()));
        }
        calculateSize(annos, schema);
        if (annos.containsKey(DecimalMin.class.getName())) {
            DecimalMin min = (DecimalMin) annos.get(DecimalMin.class.getName());
            if (min.inclusive()) {
                schema.setMinimum(BigDecimal.valueOf(Double.valueOf(min.value())));
            } else {
                schema.setExclusiveMinimum(!min.inclusive());
            }
        }
        if (annos.containsKey(DecimalMax.class.getName())) {
            DecimalMax max = (DecimalMax) annos.get(DecimalMax.class.getName());
            if (max.inclusive()) {
                schema.setMaximum(BigDecimal.valueOf(Double.valueOf(max.value())));
            } else {
                schema.setExclusiveMaximum(!max.inclusive());
            }
        }
        if (annos.containsKey(Pattern.class.getName())) {
            Pattern pattern = (Pattern) annos.get(Pattern.class.getName());
            schema.setPattern(pattern.regexp());
        }
    }

    private void calculateSize(Map<String, Annotation> annos, Schema<?> schema) {
        if (annos.containsKey(Size.class.getName())) {
            Size size = (Size) annos.get(Size.class.getName());
            if (OPENAPI_ARRAY_TYPE.equals(schema.getType())) {
                schema.setMinItems(size.min());
                schema.setMaxItems(size.max());
            } else if (OPENAPI_STRING_TYPE.equals(schema.getType())) {
                schema.setMinLength(size.min());
                schema.setMaxLength(size.max());
            }
        }
    }
}
