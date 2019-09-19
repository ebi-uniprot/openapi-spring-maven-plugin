package ebi.ac.uk.uniprot.openapi.mavenplugin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ebi.ac.uk.uniprot.openapi.mavenplugin.utils.TestUtils;
import edu.emory.mathcs.backport.java.util.Arrays;
import io.swagger.v3.core.util.Json;
import net.javacrumbs.jsonunit.JsonAssert;
import net.javacrumbs.jsonunit.core.Configuration;
import net.javacrumbs.jsonunit.core.Option;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Assert;
import org.junit.Rule;

import java.io.File;
import java.io.IOException;

public abstract class BaseAnnotationTest {
    private ObjectMapper mapper = Json.mapper();

    @Rule
    public MojoRule mojoRule = new MojoRule();

    protected void generateOAS3(String yamlFileName, String ...packageNames) throws Exception {
        String basicFolder = "src/test/resources/basic-pom";
        File pomFolder = new File(basicFolder);
        Assert.assertNotNull("Unable to get the pom folder", pomFolder);
        Assert.assertTrue("pom folder doesn't exist", pomFolder.exists());
        OpenAPIMojo openAPIMojo = (OpenAPIMojo) this.mojoRule.lookupConfiguredMojo(pomFolder, "oas-generate");
        // set the required
        openAPIMojo.setPackageToScan(Arrays.asList(packageNames));
        openAPIMojo.setOpenApiFileName(yamlFileName);
        Assert.assertNotNull("Unable to find the open api mojo", openAPIMojo);
        openAPIMojo.execute();// generate the OAS
    }

    protected void compareYamlFiles(String yamlFileName) throws IOException {
        // compare the yaml file contents
        // expected yaml file
        File expectedYamlFile = new File("src/test/resources/expected-output/" + yamlFileName);
        String expectedYamlContent = FileUtils.readFileToString(expectedYamlFile, "UTF-8");
        JsonNode expectedJson = mapper.readTree(TestUtils.yamlToJson(expectedYamlContent));

        // generated file
        File generatedYamlFile = new File("target/generated-sources/swagger/" + yamlFileName);
        Assert.assertNotNull(generatedYamlFile);
        Assert.assertTrue(generatedYamlFile.exists());
        String generatedYamlContent = FileUtils.readFileToString(generatedYamlFile, "UTF-8");
        JsonNode generatedJson = mapper.readTree(TestUtils.yamlToJson(generatedYamlContent));

        // test the content
        JsonAssert.assertJsonEquals(expectedJson, generatedJson, Configuration.empty().when(Option.IGNORING_ARRAY_ORDER));
    }
}
