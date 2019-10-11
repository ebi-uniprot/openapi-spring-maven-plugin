package uk.ac.ebi.uniprot.openapi.mavenplugin.swagger.parameter.arrayschema;

import uk.ac.ebi.uniprot.openapi.mavenplugin.swagger.parameter.resource.ParametersResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArraySchemaResource {
    @PostMapping(value = "/arrayschema", consumes = {"application/json", "application/xml"})
    @Operation(
            operationId = "subscribe",
            description = "subscribes a client to updates relevant to the requestor's account, as " +
                    "identified by the input token.  The supplied url will be used as the delivery address for response payloads",
            parameters = {
                    @Parameter(in = ParameterIn.QUERY, name = "arrayParameter", required = true, explode = Explode.TRUE,
                            array = @ArraySchema(maxItems = 10, minItems = 1,
                                    schema = @Schema(implementation = ParametersResource.SubscriptionResponse.class),
                                    uniqueItems = true
                            )
                    ),
            },
            responses = {
                    @ApiResponse(
                            description = "test description", content = @Content(
                            mediaType = "*/*",
                            schema =
                            @Schema(
                                    implementation = ParametersResource.SubscriptionResponse.class)
                    ))
            })
    public ParametersResource.SubscriptionResponse subscribe() {
        return null;
    }
}
