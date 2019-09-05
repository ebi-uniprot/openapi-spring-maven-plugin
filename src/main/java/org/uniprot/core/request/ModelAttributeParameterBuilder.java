package org.uniprot.core.request;

import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.uniprot.core.SpringDocAnnotationsUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static org.uniprot.utils.Constants.QUERY_PARAM;

public class ModelAttributeParameterBuilder {

    private final ParameterBuilder parameterBuilder;

    public ModelAttributeParameterBuilder(ParameterBuilder parameterBuilder) {
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
        return parameter;
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
}
