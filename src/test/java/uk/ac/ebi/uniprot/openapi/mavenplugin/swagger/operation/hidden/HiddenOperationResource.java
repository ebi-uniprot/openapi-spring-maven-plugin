package uk.ac.ebi.uniprot.openapi.mavenplugin.swagger.operation.hidden;

import uk.ac.ebi.uniprot.openapi.mavenplugin.swagger.resources.model.Pet;
import uk.ac.ebi.uniprot.openapi.mavenplugin.swagger.resources.model.User;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Resource With a Hidden Operation
 */
@RestController
public class HiddenOperationResource {
    @GetMapping("/hiddenbyflag")
    @Operation(operationId = "Pets", description = "Pets Example", hidden = true)
    public Pet getPet() {
        return new Pet();
    }

    @GetMapping("/hiddenbyannotation")
    @Operation(operationId = "Users", description = "Users Example")
    @Hidden
    public User getUser() {
        return new User();
    }
}
