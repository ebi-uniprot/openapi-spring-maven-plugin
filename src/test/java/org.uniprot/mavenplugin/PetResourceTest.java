package org.uniprot.mavenplugin;

import org.junit.Test;

public class PetResourceTest extends BaseResourceTest {
    @Test
    public void testGenerateOpenAPI() throws Exception {
        generateAndTestOASYaml("src/test/resources/poms/pet-resource-pom.xml",
                "src/test/resources/expected-output/pet-resource.yaml",
                "target/generated-sources/swagger/pet-resource.yaml");
    }
}
