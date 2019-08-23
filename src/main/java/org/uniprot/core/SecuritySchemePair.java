package org.uniprot.core;

import io.swagger.v3.oas.models.security.SecurityScheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Originally written by https://github.com/springdoc
 *
 */
public class SecuritySchemePair {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecuritySchemePair.class);
    private String key;
    private SecurityScheme securityScheme;

    public SecuritySchemePair(String key, SecurityScheme securityScheme) {
        super();
        this.key = key;
        this.securityScheme = securityScheme;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public SecurityScheme getSecurityScheme() {
        return securityScheme;
    }

    public void setSecurityScheme(SecurityScheme securityScheme) {
        this.securityScheme = securityScheme;
    }

}
