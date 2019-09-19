package ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.requestbody.methodandparam;

import ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.resources.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Resource with RequestBody inside Operation and another in Method
 */
@RequestMapping
public class RequestBodyParameterPriorityResource {
    @PostMapping("/requestbodyparameterpriority")
    @Operation(summary = "Create user",
            description = "This can only be done by the logged in user.",
            requestBody = @RequestBody(description = "Inside Operation"))
    @RequestBody(description = "On onlymethod")
    public ResponseEntity<User> methodWithRequestBodyAndTwoParameters(
            @RequestBody(description = "Created user object inside Parameter", required = true,
                    content = @Content(
                            schema = @Schema(implementation = User.class)))final User user) {
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
