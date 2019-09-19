package ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.operation.inheritance1;

import ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.resources.model.Pet;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * SubResource
 */
@RestController
public class SubResource implements InterfaceResource {
    @Override
    @Operation(summary = "Find pet by ID Operation in SubResource",
            description = "Returns a pet in SubResource")
    public ResponseEntity<Pet> getPetById(final Long petId) {
        return new ResponseEntity<>(new Pet(), HttpStatus.OK);
    }
}
