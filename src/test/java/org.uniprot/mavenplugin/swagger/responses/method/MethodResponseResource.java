package org.uniprot.mavenplugin.swagger.responses.method;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.uniprot.mavenplugin.swagger.resources.exception.NotFoundException;
import org.uniprot.mavenplugin.swagger.resources.model.Pet;

/**
 * Resource with a Response at Method Level
 */
@RestController
public class MethodResponseResource {
    @GetMapping("/responseinmethod")
    @Operation(summary = "Find pets",
            description = "Returns the Pets")
    @ApiResponse(responseCode = "200", description = "Status OK")
    public ResponseEntity<Pet> getPets() throws NotFoundException {
        return new ResponseEntity<>(new Pet(), HttpStatus.OK);
    }
}
