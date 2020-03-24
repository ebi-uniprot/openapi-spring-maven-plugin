package uk.ac.ebi.uniprot.openapi.extension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author sahmad
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD, ElementType.PARAMETER})
public @interface ModelFieldMeta {
    Class<? extends ModelFieldMetaReader> reader();
    String path(); // path to the json config file
}
