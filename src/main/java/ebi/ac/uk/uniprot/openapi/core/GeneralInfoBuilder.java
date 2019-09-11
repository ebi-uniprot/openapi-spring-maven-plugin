package ebi.ac.uk.uniprot.openapi.core;

import ebi.ac.uk.uniprot.openapi.utils.Constants;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

/**
 * Originally written by https://github.com/springdoc
 *
 * @author Modified by sahmad
 *
 * Default values for Info object and  Server
 * See @link https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#infoObject
 *
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#serverObject
 *
 */
public class GeneralInfoBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralInfoBuilder.class);
    private String serverBaseUrl;

    public GeneralInfoBuilder(String serverBaseUrl) {
        this.serverBaseUrl = serverBaseUrl;
    }

    public void build(OpenAPI openAPI) {
        if (openAPI.getInfo() == null) {
            Info infos = new Info().title(Constants.DEFAULT_TITLE).version(Constants.DEFAULT_VERSION);
            openAPI.setInfo(infos);
        }

        // default server value
        if (CollectionUtils.isEmpty(openAPI.getServers())) {
            Server server = new Server().url(serverBaseUrl).description(Constants.DEFAULT_SERVER_DESCRIPTION);
            openAPI.addServersItem(server);
        }


    }
}
