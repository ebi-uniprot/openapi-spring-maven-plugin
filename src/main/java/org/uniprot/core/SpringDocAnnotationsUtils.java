package org.uniprot.core;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.core.util.AnnotationsUtils;
import io.swagger.v3.core.util.PrimitiveType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Originally written by https://github.com/springdoc
 */
public class SpringDocAnnotationsUtils extends AnnotationsUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringDocAnnotationsUtils.class);

    private SpringDocAnnotationsUtils() {
        super();
    }

    static final String COMPONENTS_REF = "#/components/schemas/";

    public static Schema resolveSchemaFromType(Class<?> schemaImplementation, Components components,
                                               JsonView jsonViewAnnotation, io.swagger.v3.oas.annotations.media.Schema schemaAnnotation) {
        Schema schemaObject;
        PrimitiveType primitiveType = PrimitiveType.fromType(schemaImplementation);
        if (primitiveType != null) {
            schemaObject = primitiveType.createProperty();
            updateSchemaFromSchemaAnnotation(schemaObject, schemaAnnotation);
        } else {
            ModelConverters mr = ModelConverters.getInstance();
            schemaObject = new Schema();
            AnnotatedType annotatedType = new AnnotatedType().type(schemaImplementation).jsonViewAnnotation(jsonViewAnnotation);
            ResolvedSchema resolvedSchema = mr.readAllAsResolvedSchema(annotatedType);
            Map<String, Schema> schemaMap;
            if (resolvedSchema != null) {
                schemaMap = resolvedSchema.referencedSchemas;
                schemaMap.forEach((key, referencedSchema) -> {
                    if (components != null) {
                        components.addSchemas(key, referencedSchema);
                    }
                });
                if (StringUtils.isNotBlank(resolvedSchema.schema.getName())) {
                    schemaObject.set$ref(COMPONENTS_REF + resolvedSchema.schema.getName());
                } else {
                    schemaObject = resolvedSchema.schema;
                }
            }
        }
        if (StringUtils.isBlank(schemaObject.get$ref()) && StringUtils.isBlank(schemaObject.getType())) {
            // default to string
            schemaObject.setType("string");
        }
        return schemaObject;
    }

    public static Optional<Content> getContent(io.swagger.v3.oas.annotations.media.Content[] annotationContents,
                                               String[] classTypes, String[] methodTypes, Schema schema, Components components,
                                               JsonView jsonViewAnnotation) {
        if (annotationContents == null || annotationContents.length == 0) {
            return Optional.empty();
        }
        // Encapsulating Content model
        Content content = new Content();
        for (io.swagger.v3.oas.annotations.media.Content annotationContent : annotationContents) {
            MediaType mediaType = getMediaType(schema, components, jsonViewAnnotation, annotationContent);
            ExampleObject[] examples = annotationContent.examples();
            setExamples(mediaType, examples);
            addExtension(annotationContent, mediaType);
            io.swagger.v3.oas.annotations.media.Encoding[] encodings = annotationContent.encoding();
            addEncodingToMediaType(jsonViewAnnotation, mediaType, encodings);
            if (StringUtils.isNotBlank(annotationContent.mediaType())) {
                content.addMediaType(annotationContent.mediaType(), mediaType);
            } else {
                applyTypes(classTypes, methodTypes, content, mediaType);
            }
        }

        if (content.size() == 0) {
            return Optional.empty();
        }
        return Optional.of(content);
    }

    private static void addEncodingToMediaType(JsonView jsonViewAnnotation, MediaType mediaType,
                                               io.swagger.v3.oas.annotations.media.Encoding[] encodings) {
        for (io.swagger.v3.oas.annotations.media.Encoding encoding : encodings) {
            addEncodingToMediaType(mediaType, encoding, jsonViewAnnotation);
        }
    }

    private static void addExtension(io.swagger.v3.oas.annotations.media.Content annotationContent,
                                     MediaType mediaType) {
        if (annotationContent.extensions() != null && annotationContent.extensions().length > 0) {
            Map<String, Object> extensions = SpringDocAnnotationsUtils.getExtensions(annotationContent.extensions());
            if (extensions != null) {
                for (Map.Entry<String, Object> entry : extensions.entrySet()) {
                    mediaType.addExtension(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    private static void setExamples(MediaType mediaType, ExampleObject[] examples) {
        if (examples.length == 1 && StringUtils.isBlank(examples[0].name())) {
            getExample(examples[0], true).ifPresent(exampleObject -> mediaType.example(exampleObject.getValue()));
        } else {
            for (ExampleObject example : examples) {
                getExample(example).ifPresent(exampleObject -> mediaType.addExamples(example.name(), exampleObject));
            }
        }
    }

    private static MediaType getMediaType(Schema schema, Components components, JsonView jsonViewAnnotation,
                                          io.swagger.v3.oas.annotations.media.Content annotationContent) {
        MediaType mediaType = new MediaType();
        if (!annotationContent.schema().hidden()) {
            if (components != null) {
                getSchema(annotationContent, components, jsonViewAnnotation).ifPresent(mediaType::setSchema);
            } else {
                mediaType.setSchema(schema);
            }
        }
        return mediaType;
    }

    private static void updateSchemaFromSchemaAnnotation(Schema schema, io.swagger.v3.oas.annotations.media.Schema schemaAnnotation) {
        if (schema != null && schemaAnnotation != null) {

            if(schemaAnnotation instanceof io.swagger.v3.oas.annotations.media.ArraySchema){
                updateSchemaFromArraySchemaAnnotation(schema, (ArraySchema) schemaAnnotation);
            }

            if (StringUtils.isBlank(schema.getName()) && StringUtils.isNotBlank(schemaAnnotation.name())) {
                schema.name(schemaAnnotation.name());
            }

            if (StringUtils.isBlank(schema.getDescription()) && StringUtils.isNotBlank(schemaAnnotation.description())) {
                schema.description(schemaAnnotation.description());
            }
            String title = schema.getTitle();
            if (StringUtils.isBlank(title) && StringUtils.isNotBlank(schemaAnnotation.title())) {
                schema.title(schemaAnnotation.title());
            }
            String format = schema.getFormat();
            if (StringUtils.isBlank(format) && StringUtils.isNotBlank(schemaAnnotation.format())) {
                schema.format(schemaAnnotation.format());
            }

            Object example = schema.getExample();
            if (example == null && StringUtils.isNotBlank(schemaAnnotation.example())) {
                schema.example(schemaAnnotation.example());
            }
            Boolean readOnly = schema.getReadOnly();
            Boolean schemaAnnotReadOnly = getReadOnly(schemaAnnotation);
            if (readOnly == null && schemaAnnotReadOnly != null) {
                schema.readOnly(schemaAnnotReadOnly);
            }
            Boolean nullable = schema.getNullable();
            if (nullable == null && schemaAnnotation.nullable()) {
                schema.nullable(schemaAnnotation.nullable());
            }
            BigDecimal multipleOf = schema.getMultipleOf();
            if (multipleOf == null && schemaAnnotation.multipleOf() != 0) {
                schema.multipleOf(BigDecimal.valueOf(schemaAnnotation.multipleOf()));
            }
            Integer maxLength = schema.getMaxLength();
            if (maxLength == null && schemaAnnotation.maxLength() != Integer.MAX_VALUE) {
                schema.maxLength(schemaAnnotation.maxLength());
            }
            Integer minLength = schema.getMinLength();
            if (minLength == null && schemaAnnotation.minLength() != 0) {
                schema.minLength(schemaAnnotation.minLength());
            }
            BigDecimal minimum = schema.getMinimum();
            if (minimum == null && StringUtils.isNotBlank(schemaAnnotation.minimum())) {
                schema.minimum(new BigDecimal(schemaAnnotation.minimum()));
            }
            BigDecimal maximum = schema.getMaximum();
            if (maximum == null && StringUtils.isNotBlank(schemaAnnotation.maximum())) {
                schema.maximum(new BigDecimal(schemaAnnotation.maximum()));
            }
            Boolean exclusiveMinimum = schema.getExclusiveMinimum();
            if (exclusiveMinimum == null && schemaAnnotation.exclusiveMinimum()) {
                schema.exclusiveMinimum(schemaAnnotation.exclusiveMinimum());
            }
            Boolean exclusiveMaximum = schema.getExclusiveMaximum();
            if (exclusiveMaximum == null && schemaAnnotation.exclusiveMaximum()) {
                schema.exclusiveMaximum(schemaAnnotation.exclusiveMaximum());
            }
            String pattern = schema.getPattern();
            if (StringUtils.isBlank(pattern) && StringUtils.isNotBlank(schemaAnnotation.pattern())) {
                schema.pattern(schemaAnnotation.pattern());
            }
            Integer minProperties = schema.getMinProperties();
            if (minProperties == null && schemaAnnotation.minProperties() != 0) {
                schema.minProperties(schemaAnnotation.minProperties());
            }
            Integer maxProperties = schema.getMaxProperties();
            if (maxProperties == null && schemaAnnotation.maxProperties() != 0) {
                schema.maxProperties(schemaAnnotation.maxProperties());
            }
            List<String> requiredProperties = schema.getRequired();
            if (CollectionUtils.isEmpty(requiredProperties) && schemaAnnotation.requiredProperties().length > 0) {
                schema.setRequired(Arrays.asList(schemaAnnotation.requiredProperties()));
            }
            Boolean writeOnly = schema.getWriteOnly();
            if (writeOnly == null && schemaAnnotation.writeOnly()) {
                schema.writeOnly(schemaAnnotation.writeOnly());
            }
            ExternalDocumentation externalDocs = schema.getExternalDocs();
            if (externalDocs == null) {
                io.swagger.v3.oas.annotations.ExternalDocumentation externalDocAnnot = schemaAnnotation.externalDocs();
                if (externalDocAnnot != null &&
                        (StringUtils.isNotBlank(externalDocAnnot.description()) || StringUtils.isNotBlank(externalDocAnnot.url()))) {
                    externalDocs = new ExternalDocumentation();
                    externalDocs.description(externalDocAnnot.description());
                    externalDocs.url(externalDocAnnot.url());
                    schema.externalDocs(externalDocs);
                }
            }
            Boolean deprecated = schema.getDeprecated();
            if (deprecated == null && schemaAnnotation.deprecated()) {
                schema.deprecated(schemaAnnotation.deprecated());
            }
            List<String> allowableValues = schema.getEnum();
            if (CollectionUtils.isEmpty(allowableValues) && schemaAnnotation.allowableValues().length > 0) {
                schema.setEnum(Arrays.asList(schemaAnnotation.allowableValues()));
            }

            Map<String, Object> extensions = schema.getExtensions();
            if (CollectionUtils.isEmpty(extensions)) {
                if (schemaAnnotation.extensions() != null && (schemaAnnotation.extensions().length > 0)){
                    schema.setExtensions(AnnotationsUtils.getExtensions(schemaAnnotation.extensions()));
                }
            }
        }
    }
    private static void updateSchemaFromArraySchemaAnnotation(Schema schema, io.swagger.v3.oas.annotations.media.ArraySchema arrSchemaAnnotation) {
        Integer minItems = schema.getMinItems();
        if (minItems == null && arrSchemaAnnotation.minItems() != Integer.MAX_VALUE) {
            schema.minItems(arrSchemaAnnotation.minItems());
        }
        Integer maxItems = schema.getMaxItems();
        if (maxItems == null && arrSchemaAnnotation.maxItems() != Integer.MIN_VALUE) {
            schema.maxItems(arrSchemaAnnotation.maxItems());
        }
        Boolean uniqueItems = schema.getUniqueItems();
        if (uniqueItems == null && arrSchemaAnnotation.uniqueItems()) {
            schema.uniqueItems(arrSchemaAnnotation.uniqueItems());
        }
        Map<String, Object> extensions = schema.getExtensions();
        if (CollectionUtils.isEmpty(extensions) && arrSchemaAnnotation.extensions().length > 0) {
            schema.extensions(AnnotationsUtils.getExtensions(arrSchemaAnnotation.extensions()));
        }
    }


    private static Boolean getReadOnly(io.swagger.v3.oas.annotations.media.Schema schema) {
        if (schema != null && schema.accessMode().equals(io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY)) {
            return true;
        } else if (schema != null && schema.accessMode().equals(io.swagger.v3.oas.annotations.media.Schema.AccessMode.WRITE_ONLY)) {
            return null;
        } else if (schema != null && schema.accessMode().equals(io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_WRITE)) {
            return null;
        } else if (schema != null && schema.readOnly()) {
            return schema.readOnly();
        }
        return null;
    }
}