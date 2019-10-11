package uk.ac.ebi.uniprot.openapi.mavenplugin;

import org.junit.Test;

public class LinkAnnotationTest extends BaseAnnotationTest {
    @Test
    public void testLinkAnnotation() throws Exception {
        // generate OAS for the given package
        generateOAS3( "link-annotation.yaml",
                "uk.ac.ebi.uniprot.openapi.mavenplugin.swagger.link");

        // compare the result
        compareYamlFiles("link-annotation.yaml");
    }
}
