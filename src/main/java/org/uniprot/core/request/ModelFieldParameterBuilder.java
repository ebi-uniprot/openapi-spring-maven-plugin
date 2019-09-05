package org.uniprot.core.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.uniprot.core.SpringDocAnnotationsUtils;
import org.uniprot.extension.ModelFieldMeta;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static org.uniprot.utils.Constants.QUERY_PARAM;

/**
 * @author sahmad
 * Converts the fields of a model class in parameters
 */

public class ModelFieldParameterBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelFieldParameterBuilder.class);

    private final ParameterBuilder parameterBuilder;

    public ModelFieldParameterBuilder(ParameterBuilder parameterBuilder) {
        this.parameterBuilder = parameterBuilder;
    }

    List<Parameter> buildParametersFromModelAttribute(java.lang.reflect.Parameter modelAttribParameter) {
        List<Parameter> parameters = new ArrayList<>();
        // get the field from ModelAttribute class and its parent
        List<Field> fields = getFieldsFromClass(modelAttribParameter.getType());

        if(!CollectionUtils.isEmpty(fields)){
            parameters = fields
                    .stream()
                    .map(field -> buildParameterFromField(field))
                    .filter(parameter -> parameter.isPresent())
                    .map(parameter -> parameter.get())
                    .collect(Collectors.toList());
        }

        return parameters;
    }

    public boolean isValidModelAttribute(Type paramType, ModelAttribute modelAttribute) {
        return modelAttribute != null && !BeanUtils.isSimpleProperty(TypeUtils.getRawType(paramType, null));
    }

    private Optional<Parameter> buildParameterFromField(Field field) {
        Parameter parameter = null;
        // get the Parameter Annotation from the field
        io.swagger.v3.oas.annotations.Parameter parameterAnnotation = field.getAnnotation(io.swagger.v3.oas.annotations.Parameter.class);
        if (parameterAnnotation != null) {
            if (parameterAnnotation.hidden()) {
                return Optional.empty();
            }
            parameter = this.parameterBuilder.buildParameterFromDoc(parameterAnnotation, null);
        }

        parameter = buildParam(QUERY_PARAM, field, parameter);

        return Optional.of(parameter);
    }

    private Parameter buildParam(String in, Field field, Parameter parameter) {
        if (parameter == null) {
            parameter = new Parameter();
        }
        parameter.setIn(in);

        if (StringUtils.isEmpty(parameter.getName())) {
            parameter.setName(field.getName());
        }
        Schema<?> schema = SpringDocAnnotationsUtils.resolveSchemaFromType(field.getType(), null, null, null);
        parameter.setSchema(schema);

        Map<String, Object> extensions = getParameterExtension(field);
        if(!CollectionUtils.isEmpty(extensions)) {
            parameter.setExtensions(extensions);
        }

        parameterBuilder.applyBeanValidatorAnnotations(parameter, Arrays.asList(field.getAnnotations()));


        return parameter;
    }

    private Map<String, Object> getParameterExtension(Field field) {
        Map<String, Object> extensions = new LinkedHashMap<>();
        List<Map<String, Object>> paramMetaList = getParamMetaList(field);
        if(!CollectionUtils.isEmpty(paramMetaList)){
            extensions.put("x-param-extra", paramMetaList);
        }

        return extensions;
    }


    private List<Field> getFieldsFromClass(Class<?> cls) {
        if (cls == null || Object.class.equals(cls)) {
            return Collections.emptyList();
        }
        List<Field> fields = new ArrayList<>();
        Set<String> fieldNames = new HashSet<>();
        for (Field field : cls.getDeclaredFields()) {
            fields.add(field);
            fieldNames.add(field.getName());
        }
        for (Field field : getFieldsFromClass(cls.getSuperclass())) {
            if (!fieldNames.contains(field.getName())) {
                fields.add(field);
            }
        }
        return fields;
    }

    private List<Map<String, Object>> getParamMetaList(Field field){
        List<Map<String, Object>> modelFieldMetaList = new ArrayList<>();
        ModelFieldMeta modelFieldMeta = field.getAnnotation(ModelFieldMeta.class);

        if(modelFieldMeta != null){
            String path = modelFieldMeta.path();
            File file = new File(path);
            try {
                String jsonString = FileUtils.readFileToString(file, "UTF-8");
                ObjectMapper mapper = new ObjectMapper();
                modelFieldMetaList = mapper.readValue(jsonString, new TypeReference<List<Map<String, Object>>>(){});
            } catch (IOException e) {
                LOGGER.warn("Unable to read file {}", file);
            }
        }
        return modelFieldMetaList;
    }
}
