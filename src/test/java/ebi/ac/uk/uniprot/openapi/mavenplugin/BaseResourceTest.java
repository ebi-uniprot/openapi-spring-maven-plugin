package ebi.ac.uk.uniprot.openapi.mavenplugin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ebi.ac.uk.uniprot.openapi.mavenplugin.utils.TestUtils;
import io.swagger.v3.core.util.Json;
import net.javacrumbs.jsonunit.JsonAssert;
import net.javacrumbs.jsonunit.core.Configuration;
import net.javacrumbs.jsonunit.core.Option;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Assert;

import java.io.File;

public abstract class BaseResourceTest extends AbstractMojoTestCase {
    private ObjectMapper mapper = Json.mapper();


    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    protected void generateAndTestOAS3Yaml(String pomFilePath, String expectedFilePath, String generatedFilePath) throws Exception {
        File testPom = getTestFile(pomFilePath);
        Assert.assertNotNull(testPom);
        Assert.assertTrue(testPom.exists());
        OpenAPIMojo openAPIMojo = (OpenAPIMojo) lookupMojo("oas-generate", testPom);
        openAPIMojo.execute();// generate yaml file
        // compare the yaml file contents
        // expected yaml file
        File expectedYamlFile = new File(expectedFilePath);
        String expectedYamlContent = FileUtils.readFileToString(expectedYamlFile, "UTF-8");
        JsonNode expectedJson = mapper.readTree(TestUtils.yamlToJson(expectedYamlContent));

        // generated file
        File generatedYamlFile = new File(generatedFilePath);
        Assert.assertNotNull(generatedYamlFile);
        Assert.assertTrue(generatedYamlFile.exists());
        String generatedYamlContent = FileUtils.readFileToString(generatedYamlFile, "UTF-8");
        JsonNode generatedJson = mapper.readTree(TestUtils.yamlToJson(generatedYamlContent));

        // test the content
        JsonAssert.assertJsonEquals(expectedJson, generatedJson, Configuration.empty().when(Option.IGNORING_ARRAY_ORDER));
    }
}
