package org.uniprot.mavenplugin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import net.javacrumbs.jsonunit.JsonAssert;
import net.javacrumbs.jsonunit.core.Configuration;
import net.javacrumbs.jsonunit.core.Option;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.uniprot.mavenplugin.utils.TestUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.uniprot.utils.Constants.*;

public class OASIntegrationTest {
    private ObjectMapper mapper = Json.mapper();

    @Rule
    public MojoRule mojoRule = new MojoRule();

    @Test
    public void testExternalDocumentationResource() throws Exception {
        // generate OAS for the given package
        generateOAS("org.uniprot.mavenplugin.swagger.operation.externaldoc", "externaldoc-resource.yaml");

        // compare the result
        compareYamlFiles("externaldoc-resource.yaml");
    }

    @Test
    public void testFullyAnnotatedesource() throws Exception {
        // generate OAS for the given package
        generateOAS("org.uniprot.mavenplugin.swagger.operation.fullyannotated", "fully-annot-resource.yaml");

        // compare the result
        compareYamlFiles("fully-annot-resource.yaml");
    }

    @Test
    public void testHiddenResource() throws Exception {
        // generate OAS for the given package
        generateOAS("org.uniprot.mavenplugin.swagger.operation.hidden", "hidden-resource.yaml");

        // compare the result
        compareYamlFiles("hidden-resource.yaml");
    }

    @Test
    public void testNotAnnottSameResource() throws Exception {
        // generate OAS for the given package
        generateOAS("org.uniprot.mavenplugin.swagger.operation.samepath.notannot", "not-annot-same-name-resource.yaml");

        // compare the result
        compareYamlFiles("not-annot-same-name-resource.yaml");
    }

    @Test
    public void testOpWithoutAnnot() throws Exception {
        // generate OAS for the given package
        generateOAS("org.uniprot.mavenplugin.swagger.operation.noannot", "operation-no-annot-resource.yaml");

        // compare the result
        compareYamlFiles("operation-no-annot-resource.yaml");
    }

    @Ignore //FIXME
    @Test
    public void testServerAnnotResource() throws Exception {
        // generate OAS for the given package
        generateOAS("org.uniprot.mavenplugin.swagger.operation.server", "server-annot-resource.yaml");

        // compare the result
        compareYamlFiles("server-annot-resource.yaml");
    }

    @Test
    public void testControllerInheritance1() throws Exception {
        // generate OAS for the given package
        generateOAS("org.uniprot.mavenplugin.swagger.operation.inheritance1", "inher1-resource.yaml");

        // compare the result
        compareYamlFiles("inher1-resource.yaml");
    }

    @Test
    public void testControllerInheritance2() throws Exception {
        // generate OAS for the given package
        generateOAS("org.uniprot.mavenplugin.swagger.operation.inheritance2", "inher2-resource.yaml");

        // compare the result
        compareYamlFiles("inher2-resource.yaml");
    }

    private void generateOAS(String packageName, String yamlFileName) throws Exception {
        String basicFolder = "src/test/resources/basic-pom";
        File pomFolder = new File(basicFolder);
        Assert.assertNotNull("Unable to get the pom folder", pomFolder);
        Assert.assertTrue("pom folder doesn't exist", pomFolder.exists());
        OpenAPIMojo openAPIMojo = (OpenAPIMojo) this.mojoRule.lookupConfiguredMojo(pomFolder, "oas-generate");
        // set the required
        openAPIMojo.setPackageToScan(packageName);
        openAPIMojo.setOpenApiFileName(yamlFileName);
        Assert.assertNotNull("Unable to find the open api mojo", openAPIMojo);
        openAPIMojo.execute();// generate the OAS
    }

    private void compareYamlFiles(String yamlFileName) throws IOException {
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
