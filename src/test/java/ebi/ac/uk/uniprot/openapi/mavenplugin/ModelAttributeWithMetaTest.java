package ebi.ac.uk.uniprot.openapi.mavenplugin;

import org.junit.Test;

public class ModelAttributeWithMetaTest extends BaseAnnotationTest {
    @Test
    public void testModelAttributeParameter() throws Exception {
        // generate OAS for the given package
        generateOAS3( "model-attribute-meta.yaml",
                "ebi.ac.uk.uniprot.openapi.mavenplugin.spring.modelattribwithmeta");

        // compare the result
        compareYamlFiles("model-attribute-meta.yaml");
    }


}
