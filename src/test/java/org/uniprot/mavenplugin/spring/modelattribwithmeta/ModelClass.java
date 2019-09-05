package org.uniprot.mavenplugin.spring.modelattribwithmeta;

import org.uniprot.extension.ModelFieldMeta;

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
