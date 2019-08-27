package org.uniprot.mavenplugin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Json;
import net.javacrumbs.jsonunit.JsonAssert;
import net.javacrumbs.jsonunit.core.Configuration;
import net.javacrumbs.jsonunit.core.Option;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.uniprot.mavenplugin.utils.TestUtils;

import java.io.File;

public class EchoResourceTest extends AbstractMojoTestCase {
    private ObjectMapper mapper = Json.mapper();

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testGenerateEmptyRootPath() throws Exception {
        File testPom = getTestFile("src/test/resources/poms/echo-resource-pom.xml");
        Assert.assertNotNull(testPom);
        Assert.assertTrue(testPom.exists());
        OpenAPIMojo openAPIMojo = (OpenAPIMojo) lookupMojo("oas-generate", testPom);
        openAPIMojo.execute();
        // compare the yaml file contents
        // expected yaml file
        File expectedYamlFile = new File("src/test/resources/output/echo-resource.yaml");
        String expectedYamlContent = FileUtils.readFileToString(expectedYamlFile, "UTF-8");
        JsonNode expectedJson = mapper.readTree(TestUtils.yamlToJson(expectedYamlContent));

        // generated file
        File generatedYamlFile = new File("target/generated-sources/swagger/echo-resource.yaml");
        Assert.assertNotNull(generatedYamlFile);
        Assert.assertTrue(generatedYamlFile.exists());
        String generatedYamlContent = FileUtils.readFileToString(generatedYamlFile, "UTF-8");
        JsonNode generatedJson = mapper.readTree(TestUtils.yamlToJson(generatedYamlContent));


        JsonAssert.assertJsonEquals(expectedJson, generatedJson, Configuration.empty().when(Option.IGNORING_ARRAY_ORDER));
    }
}
