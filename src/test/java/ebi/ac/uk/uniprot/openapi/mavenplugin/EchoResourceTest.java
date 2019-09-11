package ebi.ac.uk.uniprot.openapi.mavenplugin;

import org.junit.Test;

public class EchoResourceTest extends BaseResourceTest {
    @Test
    public void testGenerateOpenAPI() throws Exception {
        generateAndTestOAS3Yaml("src/test/resources/poms/echo-resource-pom.xml",
                "src/test/resources/expected-output/echo-resource.yaml",
                "target/generated-sources/swagger/echo-resource.yaml");
    }
}
