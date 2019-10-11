package uk.ac.ebi.uniprot.openapi.mavenplugin.swagger.operation.noannot;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Resource With a Default Operation without Annotation
 */
@RequestMapping
public class OperationWithoutAnnotationResource {
    @GetMapping("/operationwithouannotation")
    public String getUser() {
        return new String();
    }
}
