package org.uniprot.mavenplugin.swagger.tags.insideop;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Resource with a Tag at Operation Level
 */
@RestController
public class TagOperationResource {

    @GetMapping("/tagoperation")
    @Operation(tags = {"Example Tag", "Second Tag"})
    public ResponseEntity<String> getTags() {
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }
}
