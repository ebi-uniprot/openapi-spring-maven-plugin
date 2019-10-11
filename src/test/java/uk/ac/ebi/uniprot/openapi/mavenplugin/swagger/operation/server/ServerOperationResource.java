package uk.ac.ebi.uniprot.openapi.mavenplugin.swagger.operation.server;

import uk.ac.ebi.uniprot.openapi.mavenplugin.swagger.resources.model.Pet;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Resource With a Hidden Operation
 */
@Controller
public class ServerOperationResource {
    @GetMapping("/serversoperation")
    @Operation(operationId = "Pets", description = "Pets Example",
            servers = {
                    @Server(description = "server 2", url = "http://foo2")
            }
    )
    public Pet getPet() {
        return new Pet();
    }
}
