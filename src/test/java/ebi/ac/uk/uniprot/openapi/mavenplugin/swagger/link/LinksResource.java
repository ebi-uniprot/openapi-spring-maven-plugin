package ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.link;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.links.Link;
import io.swagger.v3.oas.annotations.links.LinkParameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Class with Links
 */
@RestController
public class LinksResource {
    @GetMapping("/links")
    @Operation(operationId = "getUserWithAddress",
            responses = {
                    @ApiResponse(description = "test description",
                            content = @Content(mediaType = "*/*", schema = @Schema(ref = "#/components/schemas/User")),
                            links = {
                                    @Link(
                                            name = "address",
                                            operationId = "getAddress",
                                            parameters = @LinkParameter(
                                                    name = "userId",
                                                    expression = "$request.query.userId")),
                                    @Link(
                                            name = "user",
                                            operationId = "getUser",
                                            operationRef = "#/components/links/MyLink",
                                            parameters = @LinkParameter(
                                                    name = "userId",
                                                    expression = "$request.query.userId"))
                            })}
    )
    public String getUser(@RequestParam("userId")final String userId) {
        return null;
    }

}
