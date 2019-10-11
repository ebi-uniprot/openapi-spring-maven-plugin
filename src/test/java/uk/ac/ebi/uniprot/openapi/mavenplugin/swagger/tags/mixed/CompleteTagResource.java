package uk.ac.ebi.uniprot.openapi.mavenplugin.swagger.tags.mixed;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//@Tag(name = "Second Tag") FIXME
//@Tag(name = "Fourth Tag Full", description = "desc class", externalDocs = @ExternalDocumentation(description = "docs desc class"))
//@Tag(name = "Fifth Tag Full", description = "desc class", externalDocs = @ExternalDocumentation(description = "docs desc class"))
//@Tag(name = "Sixth Tag")
@RestController
public class CompleteTagResource {

    @GetMapping("/completetags")
    @Operation(tags = {"Example Tag", "Second Tag"})
    @Tag(name = "Third Tag")
    @Tag(name = "Second Tag")
    @Tag(name = "Fourth Tag Full", description = "desc", externalDocs = @ExternalDocumentation(description = "docs desc"))
    public ResponseEntity<String> getTags() {
        return ResponseEntity.ok("ok");
    }
}
