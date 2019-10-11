package uk.ac.ebi.uniprot.openapi.mavenplugin.swagger.operation.inheritance2;

import uk.ac.ebi.uniprot.openapi.mavenplugin.swagger.resources.exception.NotFoundException;
import uk.ac.ebi.uniprot.openapi.mavenplugin.swagger.resources.model.Pet;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Resource with Complete Operations Examples
 */
@RestController
public class OperationResource implements InterfaceResource {
    @Override
    @Operation(summary = "Find pet by ID Operation in SubResource",
            description = "Returns a pet in SubResource"
    )
    public ResponseEntity<Pet> getPetById(final Long petId) {
        return new ResponseEntity<>(new Pet(), HttpStatus.OK);
    }

    @GetMapping("/operationsresource")
    @Operation(summary = "Find pet by ID",
            description = "combinatedfullyannotatedoperation/{petId}",
            operationId = "petId",
            responses = {
                    @ApiResponse(
                            description = "The pet", content = @Content(
                            schema = @Schema(implementation = Pet.class)
                    )),
                    @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
                    @ApiResponse(responseCode = "404", description = "Pet not found")
            })
    public ResponseEntity<Pet> getPetById(
            @Parameter(description = "ID of pet that needs to be fetched", required = true)
            @RequestParam("petId") final Long petId, final String message) throws NotFoundException {
        return new ResponseEntity<>(new Pet(), HttpStatus.OK);
    }

    @PostMapping("/operationsresource")
    public String getUser(final String id) {
        return new String();
    }

    @PutMapping("/operationsresource")
    @Operation(operationId = "combinated sameOperationName",
            description = "combinatedsameOperationName")
    public String getPerson() {
        return new String();
    }

    @RequestMapping(value = "/operationsresource", method = RequestMethod.HEAD)
    @Operation(operationId = "combinatedsameOperationNameDuplicated",
            description = "combinatedsameOperationNameDuplicated")
    public String getPerson(final String id) {
        return new String();
    }

    @GetMapping("/operationsresource2")
    public String getUser() {
        return new String();
    }
}
