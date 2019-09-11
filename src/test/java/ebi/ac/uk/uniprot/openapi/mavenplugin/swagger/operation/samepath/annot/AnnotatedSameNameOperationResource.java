package ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.operation.samepath.annot;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Resource With a Default Operation without Annotation
 */
@Controller
public class AnnotatedSameNameOperationResource {
    @GetMapping("/sameOperationName")
    @Operation(description = "Same Operation Name")
    public String getUser() {
        return new String();
    }

    @DeleteMapping("/sameOperationName")
    @Operation(description = "Same Operation Name Duplicated")
    public String getUser(final String id) {
        return new String();
    }
}
