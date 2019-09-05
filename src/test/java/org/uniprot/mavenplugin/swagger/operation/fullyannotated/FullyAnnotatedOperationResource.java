package org.uniprot.mavenplugin.swagger.operation.fullyannotated;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.uniprot.mavenplugin.swagger.resources.exception.NotFoundException;
import org.uniprot.mavenplugin.swagger.resources.model.Pet;

/**
 * Resource with Operations Examples
 */
@RestController
public class FullyAnnotatedOperationResource {
    @GetMapping("/fullyannotatedoperation/{petId}")
    @Operation(summary = "Find pet by ID",
            description = "Returns a pet when 0 < ID <= 10.  ID > 10 or non integers will simulate API error conditions",
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
            @PathVariable("petId")final Long petId) throws NotFoundException {
        return new ResponseEntity<>(new Pet(), HttpStatus.OK);
    }
}
