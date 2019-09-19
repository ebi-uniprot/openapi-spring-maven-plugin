package ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.operation.externaldoc;

import ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.resources.exception.NotFoundException;
import ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.resources.model.Pet;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Resource with Operations Examples
 */
@RestController
public class ExternalDocumentationResource {
    @GetMapping("/{petId}")
    @Operation(summary = "Find pet by ID",
            description = "Returns a pet when 0 < ID <= 10.  ID > 10 or non integers will simulate API error conditions",
            operationId = "petId",
            externalDocs = @ExternalDocumentation(description = "External in Operation", url = "http://url.me"))
    @ExternalDocumentation(description = "External Annotation Documentation", url = "http://url.me")
    public ResponseEntity<Pet> getPetById(
            @Parameter(description = "ID of pet that needs to be fetched", required = true)
            @PathVariable("petId") Long petId) throws NotFoundException {
        return new ResponseEntity(new Pet(), HttpStatus.OK);
    }
}
