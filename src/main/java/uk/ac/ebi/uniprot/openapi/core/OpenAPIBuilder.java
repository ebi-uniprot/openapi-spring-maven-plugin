package uk.ac.ebi.uniprot.openapi.core;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Originally written by https://github.com/springdoc
 *
 * @author Modified by sahmad
 */

public class OpenAPIBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenAPIBuilder.class);
    private OpenAPI openAPI;

    public OpenAPIBuilder() {
        this.openAPI = new OpenAPI();
        this.openAPI.setComponents(new Components());
        this.openAPI.setPaths(new Paths());
    }

    public OpenAPI getOpenAPI() {
        return this.openAPI;
    }

    public Components getComponents() {
        return this.openAPI.getComponents();
    }

    public Paths getPaths() {
        return this.openAPI.getPaths();
    }
}
