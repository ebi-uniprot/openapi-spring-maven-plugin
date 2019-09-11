/**
 * Copyright 2016 SmartBear Software
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.petstore;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.resources.data.PetData;
import ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.resources.exception.NotFoundException;
import ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.resources.model.Pet;

import java.util.List;

@RequestMapping(value = "/pet", consumes = {"application/json"}, produces = {"application/json", "application/xml"})
public class PetResource {
    static PetData petData = new PetData();

    @GetMapping(value = "/{petId}")
    @Operation(summary = "Find pet by ID",
            description = "Returns a pet when 0 < ID <= 10.  ID > 10 or nonintegers will simulate API error conditions",
            responses = {
                    @ApiResponse(
                            description = "The pet", content = @Content(
                            schema = @Schema(implementation = Pet.class)
                    )),
                    @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
                    @ApiResponse(responseCode = "404", description = "Pet not found")
            })
    public ResponseEntity<Pet> getPetById(
            @Parameter(description = "ID of pet that needs to be fetched"/*, _enum = "range[1,10]"*/, required = true)
            @PathVariable("petId") final Long petId) throws NotFoundException {
        Pet pet = petData.getPetById(petId);
        if (pet != null) {
            return new ResponseEntity(pet, HttpStatus.OK);
        } else {
            throw new NotFoundException(404, "Pet not found");
        }
    }

    @RequestMapping(method = RequestMethod.POST, consumes = {"application/json", "application/xml"})
    @Operation(summary = "Add a new pet to the store",
            responses = {
                    @ApiResponse(responseCode = "405", description = "Invalid input")
            })
    public ResponseEntity<String> addPet(
            @Parameter(description = "Pet object that needs to be added to the store", required = true) final Pet pet) {
        petData.addPet(pet);
        return ResponseEntity.ok("SUCCESS");
    }

    @PostMapping(value = "/bodynoannotation", consumes = {"application/json", "application/xml"}, produces = {"application/json", "application/xml"})
    @Operation(summary = "Add a new pet to the store no annotation",
            responses = {
                    @ApiResponse(responseCode = "405", description = "Invalid input")
            })
    public ResponseEntity<String> addPetNoAnnotation(final Pet pet) {
        petData.addPet(pet);
        return ResponseEntity.ok("SUCCESS");
    }

    @RequestMapping(value = "/bodyid", method = RequestMethod.POST, consumes = {"application/json", "application/xml"})
    @Operation(summary = "Add a new pet to the store passing an integer with generic parameter annotation",
            responses = {
                    @ApiResponse(responseCode = "405", description = "Invalid input")
            })
    public ResponseEntity<String> addPetByInteger(
            @Parameter(description = "Pet object that needs to be added to the store", required = true) final int petId) {
        return ResponseEntity.ok("SUCCESS");
    }

    @PostMapping(value = "/bodyidnoannotation", consumes = {"application/json", "application/xml"})
    @Operation(summary = "Add a new pet to the store passing an integer without parameter annotation",
            responses = {
                    @ApiResponse(responseCode = "405", description = "Invalid input")
            })
    public ResponseEntity<String> addPetByIntegerNoAnnotation(final int petId) {
        return ResponseEntity.ok("SUCCESS");
    }

    @PutMapping
    @Operation(summary = "Update an existing pet",
            responses = {
                    @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
                    @ApiResponse(responseCode = "404", description = "Pet not found"),
                    @ApiResponse(responseCode = "405", description = "Validation exception")})
    public ResponseEntity<String> updatePet(
            @Parameter(description = "Pet object that needs to be added to the store", required = true) final Pet pet) {
        petData.addPet(pet);
        return ResponseEntity.ok("SUCCESS");
    }

    @GetMapping(value = "/findByStatus", produces = "application/xml")
    @Operation(summary = "Finds Pets by status",
            description = "Multiple status values can be provided with comma seperated strings",
            responses = {
                    @ApiResponse(
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Pet.class))),
                    @ApiResponse(
                            responseCode = "400", description = "Invalid status value"
                    )}
    )
    public List<Pet> findPetsByStatus(
            @Parameter(description = "Status values that need to be considered for filter", required = true) @RequestParam("status") final String status
    ) {
        List<Pet> pets = petData.findPetByStatus(status);
        return pets;
    }

    @RequestMapping(value = "/findByTags", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "Finds Pets by tags",
            description = "Muliple tags can be provided with comma seperated strings. Use tag1, tag2, tag3 for testing.",
            responses = {
                    @ApiResponse(description = "Pets matching criteria",
                            content = @Content(schema = @Schema(implementation = Pet.class))
                    ),
                    @ApiResponse(description = "Invalid tag value", responseCode = "400")
            })
    @Deprecated
    public List<Pet> findPetsByTags(
            @Parameter(description = "Tags to filter by", required = true) @RequestParam("tags") final String tags) {
        return petData.findPetByTags(tags);
    }
}
