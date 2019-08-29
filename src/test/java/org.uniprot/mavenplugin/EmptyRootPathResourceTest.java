package org.uniprot.mavenplugin;

import org.junit.Test;

public class EmptyRootPathResourceTest extends BaseResourceTest {
    @Test
    public void testGenerateEmptyRootPath() throws Exception {
        generateAndTestOAS3Yaml("src/test/resources/poms/empty-root-path-pom.xml",
                "src/test/resources/expected-output/empty-root-path.yaml",
                "target/generated-sources/swagger/empty-root-path.yaml");
    }
}
