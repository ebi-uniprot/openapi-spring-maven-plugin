package ebi.ac.uk.uniprot.openapi.mavenplugin.empty;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author carlosjgp
 */
@RequestMapping
public class EmptyRootPathResource {
    @Operation(summary = "This is a sample summary of the api")
    @RequestMapping(value="/testingEmptyRootPathResource", method = RequestMethod.GET)
    public ResponseEntity<String> emptyRootOperationId() {
        return new ResponseEntity<>("testingEmptyRootPathResource", HttpStatus.OK);
    }
}
