package org.uniprot.mavenplugin;

import org.junit.Test;

public class EchoResourceTest extends BaseResourceTest {
    @Test
    public void testGenerateOpenAPI() throws Exception {
        generateAndTestOASYaml("src/test/resources/poms/echo-resource-pom.xml",
                "src/test/resources/expected-output/echo-resource.yaml",
                "target/generated-sources/swagger/echo-resource.yaml");
    }
}
