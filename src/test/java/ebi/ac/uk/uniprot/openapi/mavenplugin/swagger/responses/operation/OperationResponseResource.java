package ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.responses.operation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.resources.exception.NotFoundException;
import ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.resources.model.User;

/**
 * Resource with the response in the Operation Annotation
 */
@RestController
public class OperationResponseResource {
    @GetMapping("/responseinoperation")
    @Operation(summary = "Find Users",
            description = "Returns the Users",
            responses = {@ApiResponse(responseCode = "200", description = "Status OK")})
    public ResponseEntity<User> getUsers() throws NotFoundException {
        return new ResponseEntity<>(new User(), HttpStatus.OK);
    }
}
