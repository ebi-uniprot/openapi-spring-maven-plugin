package org.uniprot.core.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.uniprot.core.SpringDocAnnotationsUtils;
import org.uniprot.extension.ModelAttributeMeta;
import org.uniprot.extension.ModelAttributeMetaModel;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static org.uniprot.utils.Constants.QUERY_PARAM;

public class ModelAttributeParameterBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelAttributeParameterBuilder.class);

    private final ParameterBuilder parameterBuilder;

    public ModelAttributeParameterBuilder(ParameterBuilder parameterBuilder) {
        this.parameterBuilder = parameterBuilder;
    }

    List<Parameter> buildParametersFromModelAttribute(java.lang.reflect.Parameter modelAttribParameter) {
        List<Parameter> parameters = new ArrayList<>();
        // get the field from ModelAttribute class and its parent
        List<Field> fields = getFieldsFromClass(modelAttribParameter.getType());

        if(!CollectionUtils.isEmpty(fields)){
            // get the @ModelAttributeMeta annotation
            ModelAttributeMetaModel meta = getModelAttributeMetaModel(modelAttribParameter);

            parameters = fields
                    .stream()
                    .map(field -> buildParameterFromField(field, meta))
                    .filter(parameter -> parameter.isPresent())
                    .map(parameter -> parameter.get())
                    .collect(Collectors.toList());
        }

        return parameters;
    }

    public boolean isValidModelAttribute(Type paramType, ModelAttribute modelAttribute) {
        return modelAttribute != null && !BeanUtils.isSimpleProperty(TypeUtils.getRawType(paramType, null));
    }

    private Optional<Parameter> buildParameterFromField(Field field, ModelAttributeMetaModel meta) {
        Parameter parameter = null;
        // get the Parameter Annotation from the field
        io.swagger.v3.oas.annotations.Parameter parameterAnnotation = field.getAnnotation(io.swagger.v3.oas.annotations.Parameter.class);
        if (parameterAnnotation != null) {
            if (parameterAnnotation.hidden()) {
                return Optional.empty();
            }
            parameter = this.parameterBuilder.buildParameterFromDoc(parameterAnnotation, null);
        }

        parameter = buildParam(QUERY_PARAM, field, parameter, meta);
        return Optional.of(parameter);


    }

    private Parameter buildParam(String in, Field field, Parameter parameter, ModelAttributeMetaModel meta) {
        if (parameter == null) {
            parameter = new Parameter();
        }
        parameter.setIn(in);

        if (StringUtils.isEmpty(parameter.getName())) {
            parameter.setName(field.getName());
        }
        Schema<?> schema = SpringDocAnnotationsUtils.resolveSchemaFromType(field.getType(), null, null, null);
        parameter.setSchema(schema);

        if(meta != null) { // set the param extension
            Map<String, Object> extensions = getParameterExtension(parameter.getName(), meta);
            parameter.setExtensions(extensions);
        }

        return parameter;
    }

    private Map<String, Object> getParameterExtension(String name, ModelAttributeMetaModel meta) {
        Map<String, Object> extensions = new LinkedHashMap<>();
        if("query".equals(name) && meta.getQueryFields() != null){
            extensions.put("x-param-extra", meta.getQueryFields());
        } else if("sort".equals(name) && meta.getSortFields() != null){
            extensions.put("x-param-extra", meta.getSortFields());
        } else if("fields".equals(name) && meta.getReturnFields() != null){
            extensions.put("x-param-extra", meta.getReturnFields());
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

    private ModelAttributeMetaModel getModelAttributeMetaModel(java.lang.reflect.Parameter parameter){
        ModelAttributeMetaModel meta = null;
        ModelAttributeMeta modelAttribMeta = AnnotationUtils.getAnnotation(parameter, ModelAttributeMeta.class);
        if(modelAttribMeta != null){
            String path = modelAttribMeta.path();
            File file = new File(path);
            try {
                String jsonString = FileUtils.readFileToString(file, "UTF-8");
                ObjectMapper objectMapper = new ObjectMapper();
                TypeFactory typeFactory = objectMapper.getTypeFactory();
                meta = objectMapper.readValue(jsonString, typeFactory.constructType(ModelAttributeMetaModel.class));
            } catch (IOException e) {
                LOGGER.warn("Unable to read file {}", file);
            }
        }
        return meta;
    }
}
