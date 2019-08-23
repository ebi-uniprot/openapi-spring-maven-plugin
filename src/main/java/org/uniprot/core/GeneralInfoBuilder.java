package org.uniprot.core;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import static org.uniprot.utils.Constants.*;

/**
 * Originally written by https://github.com/springdoc
 *
 * @author Modified by sahmad
 */

public class GeneralInfoBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralInfoBuilder.class);
    private String serverBaseUrl;

    public GeneralInfoBuilder(String serverBaseUrl) {
        this.serverBaseUrl = serverBaseUrl;
    }

    public void build(OpenAPI openAPI) {
        if (openAPI.getInfo() == null) {
            Info infos = new Info().title(DEFAULT_TITLE).version(DEFAULT_VERSION);
            openAPI.setInfo(infos);
        }

        // default server value
        if (CollectionUtils.isEmpty(openAPI.getServers())) {
            Server server = new Server().url(serverBaseUrl).description(DEFAULT_SERVER_DESCRIPTION);
            openAPI.addServersItem(server);
        }


    }

    public void setServerBaseUrl(String serverBaseUrl) {
        this.serverBaseUrl = serverBaseUrl;
    }
}
