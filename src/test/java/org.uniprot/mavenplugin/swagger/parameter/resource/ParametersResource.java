package org.uniprot.mavenplugin.swagger.parameter.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.uniprot.mavenplugin.swagger.parameter.repeatable.RepeatableParametersResource;

/**
 * Resource with some Parameters examples
 */
@RestController
public class ParametersResource {
    @PostMapping(value = "/parameters", consumes = {"application/json", "application/xml"})
    @Operation(
            operationId = "subscribe",
            description = "subscribes a client to updates relevant to the requestor's account, as " +
                    "identified by the input token.  The supplied url will be used as the delivery address for response payloads",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "subscriptionId", required = true,
                            schema = @Schema(implementation = RepeatableParametersResource.SubscriptionResponse.class), style = ParameterStyle.SIMPLE),
                    @Parameter(in = ParameterIn.QUERY, name = "formId", required = true,
                            example = "Example"),
                    @Parameter(in = ParameterIn.QUERY, name = "explodeFalse", required = true, explode = Explode.FALSE,
                            schema = @Schema(implementation = ParametersResource.SubscriptionResponse.class)),
                    @Parameter(in = ParameterIn.QUERY, name = "explodeTrue", required = true, explode = Explode.TRUE,
                            schema = @Schema(implementation = ParametersResource.SubscriptionResponse.class)),
                    @Parameter(in = ParameterIn.QUERY, name = "explodeAvoiding", required = true, explode = Explode.TRUE,
                            schema = @Schema(
                                    type = "int",
                                    format = "id",
                                    description = "the generated id",
                                    accessMode = Schema.AccessMode.READ_ONLY
                            )),
                    @Parameter(in = ParameterIn.QUERY, name = "arrayParameter", required = true, explode = Explode.TRUE,
                            array = @ArraySchema(maxItems = 10, minItems = 1,
                                    schema = @Schema(implementation = ParametersResource.SubscriptionResponse.class),
                                    uniqueItems = true
                            )
                            ,
                            schema = @Schema(
                                    type = "int",
                                    format = "id",
                                    description = "the generated id",
                                    accessMode = Schema.AccessMode.READ_ONLY),
                            content = @Content(schema = @Schema(type = "number",
                                    description = "the generated id",
                                    accessMode = Schema.AccessMode.READ_ONLY))
                    ),
                    @Parameter(in = ParameterIn.QUERY, name = "arrayParameterImplementation", required = true, explode = Explode.TRUE,
                            array = @ArraySchema(maxItems = 10, minItems = 1,
                                    schema = @Schema(implementation = ParametersResource.SubscriptionResponse.class),
                                    uniqueItems = true
                            )
                    ),
                    @Parameter(in = ParameterIn.QUERY, name = "arrayParameterImplementation2", required = true, explode = Explode.TRUE,
                            schema = @Schema(implementation = ParametersResource.SubscriptionResponse.class))

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
    public ParametersResource.SubscriptionResponse subscribe(@Parameter(description = "idParam")
                                                             @RequestParam("id") final String id) {
        return null;
    }

    public static class SubscriptionResponse {
        public String subscriptionId;
    }
}
