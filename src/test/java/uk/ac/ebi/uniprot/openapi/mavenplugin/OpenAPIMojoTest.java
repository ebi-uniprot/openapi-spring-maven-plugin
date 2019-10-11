package uk.ac.ebi.uniprot.openapi.mavenplugin;

import uk.ac.ebi.uniprot.openapi.utils.Constants;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class OpenAPIMojoTest {

    @Rule
    public MojoRule mojoRule = new MojoRule();

    @Test
    public void testPluginExecuteWithSimplePom() throws Exception {
        String basicFolder = "src/test/resources/basic-pom";
        File pomFolder = new File(basicFolder);
        Assert.assertNotNull("Unable to get the pom folder", pomFolder);
        Assert.assertTrue("pom folder doesn't exist", pomFolder.exists());
        OpenAPIMojo openAPIMojo = (OpenAPIMojo) this.mojoRule.lookupConfiguredMojo(pomFolder, "oas-generate");
        Assert.assertNotNull("Unable to find the open api mojo", openAPIMojo);
        openAPIMojo.execute();

        // read the generated file
        File yamlFile = new File("target/generated-sources/swagger/openapi3.yaml");
        String yamlContent = FileUtils.readFileToString(yamlFile, "UTF-8");
        OpenAPI openAPI = Yaml.mapper().readValue(yamlContent, OpenAPI.class);

        // verify the values
        Assert.assertNotNull("openAPI object is null", openAPI);
        Assert.assertEquals("3.0.1", openAPI.getOpenapi());
        Info info = openAPI.getInfo();
        Assert.assertNotNull("Info is null", info);
        Assert.assertEquals(Constants.DEFAULT_TITLE, info.getTitle());
        Assert.assertEquals(Constants.DEFAULT_VERSION, info.getVersion());
        List<Server> servers = openAPI.getServers();
        Assert.assertEquals(1, servers.size());
        Assert.assertEquals("localhost", servers.get(0).getUrl());
        Assert.assertEquals(Constants.DEFAULT_SERVER_DESCRIPTION, servers.get(0).getDescription());
        Assert.assertTrue(openAPI.getPaths().isEmpty());
        Assert.assertNotNull(openAPI.getComponents());
        /*
        TODO add these things in info
        private String description = null;
        private String termsOfService = null;
        private Contact contact = null;
        private License license = null;
         */
    }

}
