package uk.ac.ebi.uniprot.openapi.mavenplugin;

import org.junit.Test;

public class AnnotatedSameNameOperationResourceTest extends BaseResourceTest {
    @Test
    public void testGenerateOpenAPI() throws Exception {
        String pomFilePath = "src/test/resources/poms/annotated-same-path-resource-pom.xml";
        String expectedFilePath = "src/test/resources/expected-output/annotated-same-path-resource.yaml";
        String generatedFilePath = "target/generated-sources/swagger/annotated-same-path-resource.yaml";
        generateAndTestOAS3Yaml(pomFilePath, expectedFilePath, generatedFilePath);
    }
}
