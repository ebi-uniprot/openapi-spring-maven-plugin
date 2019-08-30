package org.uniprot.mavenplugin.swagger.operation.inheritance1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.uniprot.mavenplugin.swagger.resources.model.Pet;

/**
 * Interface resource
 */
public interface InterfaceResource {
    @GetMapping("/interfaceoperation/{petId}")
    @Operation(summary = "Find pet by ID Operation in Parent",
            description = "Returns a pet in Parent")
    ResponseEntity<Pet> getPetById(@Parameter(description = "ID of pet that needs to be fetched", required = true)
                        @PathVariable("petId") final Long petId);
}
