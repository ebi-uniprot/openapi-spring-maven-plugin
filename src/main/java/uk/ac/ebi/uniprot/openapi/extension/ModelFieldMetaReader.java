package uk.ac.ebi.uniprot.openapi.extension;

import java.util.List;
import java.util.Map;

/**
 * Reader to provide extra param to the field of rest api doc ("x-param-extra")
 */
public interface ModelFieldMetaReader {
    List<Map<String, Object>> read(String metaFile);
}
