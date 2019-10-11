package uk.ac.ebi.uniprot.openapi.mavenplugin.swagger.tags.methodlevel;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TagMethodResource {
    @GetMapping("/tagsinmethod")
    @Tag(name = "Third Tag")
    @Tag(name = "Second Tag")
    @Tag(name = "Fourth Tag Full", description = "desc", externalDocs = @ExternalDocumentation(description = "docs desc"))
    public ResponseEntity<String> getTags() {
        return ResponseEntity.ok("ok");
    }
}
