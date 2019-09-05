package org.uniprot.mavenplugin;

import org.junit.Test;

public class ModelAttributeWithMetaTest extends BaseAnnotationTest {
    @Test
    public void testModelAttributeParameter() throws Exception {
        // generate OAS for the given package
        generateOAS3("org.uniprot.mavenplugin.spring.modelattribwithmeta", "model-attribute-meta.yaml");

        // compare the result
        compareYamlFiles("model-attribute-meta.yaml");
    }


}
