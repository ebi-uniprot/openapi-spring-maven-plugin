package ebi.ac.uk.uniprot.openapi.mavenplugin;

import org.junit.Test;

public class MultiPackageResourceTest extends BaseAnnotationTest {
    @Test
    public void testGenerateOpenAPIMultiplePackage() throws Exception {
        // generate OAS for the given package
        generateOAS3( "multi-package-oas.yaml",
                "ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.requestbody.general",
                "ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.petstore");

        // compare the result
        compareYamlFiles("multi-package-oas.yaml");
    }
}
