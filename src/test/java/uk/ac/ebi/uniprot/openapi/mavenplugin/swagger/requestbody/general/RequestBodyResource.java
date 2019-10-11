package uk.ac.ebi.uniprot.openapi.mavenplugin.swagger.requestbody.general;

import uk.ac.ebi.uniprot.openapi.mavenplugin.swagger.resources.model.Pet;
import uk.ac.ebi.uniprot.openapi.mavenplugin.swagger.resources.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Resource with RequestBody examples
 */
@RestController
public class RequestBodyResource {
    @PostMapping("/methodWithRequestBodyWithoutContent")
    @Operation(summary = "Create user",
            description = "This can only be done by the logged in user.")
    public ResponseEntity<User> methodWithRequestBodyWithoutContent(
            @RequestBody(description = "Created user object", required = true) final User user) {
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/methodWithRequestBodyWithoutContentWithoutImplementation")
    @Operation(summary = "Create user",
            description = "This can only be done by the logged in user.")
    public ResponseEntity<User> methodWithRequestBodyWithoutContentWithoutImplementation(
            @RequestBody(description = "Created user object", required = true,
                    content = @Content(
                            schema = @Schema(name = "User", description = "User description",
                                    example = "User Description", required = true))) final User user) {
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/methodWithRequestBodyAndTwoParameters")
    @Operation(summary = "Create user",
            description = "This can only be done by the logged in user.")
    public ResponseEntity<User> methodWithRequestBodyAndTwoParameters(
            @RequestBody(description = "Created user object", required = true,
                    content = @Content(
                            schema = @Schema(implementation = User.class))) final User user,
            @RequestParam("name") final String name, @RequestParam("code") final String code) {
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/methodWithRequestBodyWithoutAnnotation")
    @Operation(summary = "Modify user",
            description = "Modifying user.")
    public ResponseEntity<User> methodWithRequestBodyWithoutAnnotation(
            final User user) {
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/methodWithoutRequestBodyAndTwoParameters")
    @Operation(summary = "Delete user",
            description = "This can only be done by the logged in user.")
    public ResponseEntity<User> methodWithoutRequestBodyAndTwoParameters(
            @RequestParam("name") final String name, @RequestParam("code") final String code) {
        return new ResponseEntity<>(new User(), HttpStatus.OK);
    }

    @PutMapping(value = "/methodWithRequestBodyWithoutAnnotationAndTwoConsumes", consumes = {"application/json", "application/xml"})
    @Operation(summary = "Modify pet", description = "Modifying pet.")
    public ResponseEntity<User> methodWithRequestBodyWithoutAnnotationAndTwoConsumes(
            final User user) {
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping(value = "/methodWithTwoRequestBodyWithoutAnnotationAndTwoConsumes", consumes = {"application/json", "application/xml"})
    @Operation(summary = "Create pet", description = "Creating pet.")
    public ResponseEntity<Pet> methodWithTwoRequestBodyWithoutAnnotationAndTwoConsumes(
            final Pet pet, final User user) {
        return new ResponseEntity<>(pet, HttpStatus.OK);
    }

    @PostMapping(value = "/methodWithTwoRequestBodyWithAnnotationAndTwoConsumes", consumes = {"application/json", "application/xml"})
    @Operation(summary = "Create pet",
            description = "Creating pet.")
    public ResponseEntity<Pet> methodWithTwoRequestBodyWithAnnotationAndTwoConsumes(
            final @RequestBody(description = "Request Body Pet") Pet pet,
            @RequestBody(description = "Request Body User") final User user) {
        return new ResponseEntity<>(pet, HttpStatus.OK);
    }

    @DeleteMapping(value = "/methodWithOneSimpleRequestBody", consumes = {"application/json", "application/xml"})
    @Operation(summary = "Delete pet", description = "Deleting pet.")
    public ResponseEntity<Pet> methodWithOneSimpleRequestBody(final int id) {
        return new ResponseEntity<>(new Pet(), HttpStatus.OK);
    }
}
