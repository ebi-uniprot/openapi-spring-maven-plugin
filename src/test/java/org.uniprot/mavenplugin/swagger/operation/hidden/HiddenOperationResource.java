package org.uniprot.mavenplugin.swagger.operation.hidden;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.uniprot.mavenplugin.swagger.resources.model.Pet;
import org.uniprot.mavenplugin.swagger.resources.model.User;

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
