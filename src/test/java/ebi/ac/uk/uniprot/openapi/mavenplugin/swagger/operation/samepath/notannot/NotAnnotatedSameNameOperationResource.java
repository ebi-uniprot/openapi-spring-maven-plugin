package ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.operation.samepath.notannot;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Resource With a Default Operation without Annotation
 */
@RestController
public class NotAnnotatedSameNameOperationResource {
    @GetMapping("/notannotatedoperation")
    public String getUser() {
        return new String();
    }

    @GetMapping("/notannotatedoperationduplicated")
    public String getUser(final String id) {
        return new String();
    }
}
