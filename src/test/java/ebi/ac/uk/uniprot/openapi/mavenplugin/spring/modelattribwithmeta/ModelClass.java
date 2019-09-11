package ebi.ac.uk.uniprot.openapi.mavenplugin.spring.modelattribwithmeta;

import ebi.ac.uk.uniprot.openapi.extension.ModelFieldMeta;

public class ModelClass {
    @ModelFieldMeta(path = "src/test/resources/query_param_meta.json")
    private String query;
    @ModelFieldMeta(path = "src/test/resources/return_field_meta.json")
    private String fields;
    @ModelFieldMeta(path = "src/test/resources/sort_param_meta.json")
    private String sort;
    private String otherField;
    private int size;
}
