package uk.ac.ebi.uniprot.openapi.mavenplugin;

import org.junit.Test;

public class ExamplesResourceTest extends BaseResourceTest {
    @Test
    public void testGenerateOpenAPI() throws Exception {
        generateAndTestOAS3Yaml("src/test/resources/poms/examples-resource-pom.xml",
                "src/test/resources/expected-output/examples-resource.yaml",
                "target/generated-sources/swagger/examples-resource.yaml");
    }
}
