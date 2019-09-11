package ebi.ac.uk.uniprot.openapi.core;

import io.swagger.v3.core.util.AnnotationsUtils;
import io.swagger.v3.core.util.ReflectionUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
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

public class TagsBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(TagsBuilder.class);

    public Operation build(Method handlerMethod, Operation operation, OpenAPI openAPI) {

        // class tags
        List<Tag> classTags = ReflectionUtils
                .getRepeatableAnnotations(handlerMethod.getDeclaringClass(), Tag.class);

        // method tags
        List<Tag> methodTags = ReflectionUtils
                .getRepeatableAnnotations(handlerMethod, Tag.class);

        List<Tag> allTags = new ArrayList<>();
        Set<String> tagsStr = new HashSet<>();

        if (!CollectionUtils.isEmpty(methodTags)) {
            tagsStr.addAll(methodTags.stream().map(Tag::name).collect(Collectors.toSet()));
            allTags.addAll(methodTags);
        }

        if (!CollectionUtils.isEmpty(classTags)) {
            tagsStr.addAll(classTags.stream().map(Tag::name).collect(Collectors.toSet()));
            allTags.addAll(classTags);
        }

        Optional<Set<io.swagger.v3.oas.models.tags.Tag>> tags = AnnotationsUtils
                .getTags(allTags.toArray(new Tag[allTags.size()]), true);

        if (tags.isPresent()) {
            Set<io.swagger.v3.oas.models.tags.Tag> tagsSet = tags.get();
            // Existing tags
            List<io.swagger.v3.oas.models.tags.Tag> openApiTags = openAPI.getTags();
            if (!CollectionUtils.isEmpty(openApiTags))
                tagsSet.addAll(openApiTags);
            openAPI.setTags(new ArrayList<>(tagsSet));
        }

        if (!CollectionUtils.isEmpty(tagsStr)) {
            operation.setTags(new ArrayList<>(tagsStr));
        }

        return operation;
    }
}
