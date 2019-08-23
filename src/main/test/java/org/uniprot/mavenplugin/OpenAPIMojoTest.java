package org.uniprot.mavenplugin;

import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

public class OpenAPIMojoTest {

    @Test
    public void testGetOpenAPI() throws Exception{
        OpenAPIMojo openAPIMojo = new OpenAPIMojo();
        openAPIMojo.setPackageToScan("org.uniprot.mavenplugin");
        OpenAPI openAPI = openAPIMojo.getOpenApi();
        System.out.println(Yaml.mapper().writeValueAsString(openAPI));
    }

    @RestController("/some/path")
    private static class SomeResourceWithClassOnlyPaths {

        // GET /some/path (explicit value="")
        @RequestMapping(value="get/path", method= RequestMethod.GET)
        public String getSomething() { return null; }

        // POST /some/path (value=null)
        @RequestMapping(value="post/path", method={RequestMethod.POST, RequestMethod.GET})
        public void postSomething() { }

        // GET /some/path/search
        @RequestMapping(value="/search/path", method=RequestMethod.GET)
        public String searchSomething() { return null; }
    }
}
