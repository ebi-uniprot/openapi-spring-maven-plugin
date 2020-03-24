package uk.ac.ebi.uniprot.openapi.mavenplugin.spring.modelattribwithmeta;

import uk.ac.ebi.uniprot.openapi.extension.ModelFieldMeta;
import uk.ac.ebi.uniprot.openapi.mavenplugin.TestModelFieldMetaReaderImpl;

public class ModelClass {
    @ModelFieldMeta(reader = TestModelFieldMetaReaderImpl.class, path = "src/test/resources/query_param_meta.json")
    private String query;
    @ModelFieldMeta(reader = TestModelFieldMetaReaderImpl.class, path = "src/test/resources/return_field_meta.json")
    private String fields;
    @ModelFieldMeta(reader = TestModelFieldMetaReaderImpl.class, path = "src/test/resources/sort_param_meta.json")
    private String sort;
    private String otherField;
    private int size;
}
