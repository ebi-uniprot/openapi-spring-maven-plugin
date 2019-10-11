package uk.ac.ebi.uniprot.openapi.mavenplugin;

import org.junit.Test;

public class ModelAttributeAnnotationTest extends BaseAnnotationTest {
    @Test
    public void testModelAttributeParameter() throws Exception {
        // generate OAS for the given package
        generateOAS3("model-attribute.yaml",
                "uk.ac.ebi.uniprot.openapi.mavenplugin.spring.modelattribute");

        // compare the result
        compareYamlFiles("model-attribute.yaml");
    }


}
