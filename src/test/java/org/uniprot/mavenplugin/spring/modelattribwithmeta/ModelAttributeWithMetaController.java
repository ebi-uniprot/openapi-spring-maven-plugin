package org.uniprot.mavenplugin.spring.modelattribwithmeta;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;
import org.uniprot.extension.ModelAttributeMeta;

@RestController
public class ModelAttributeWithMetaController {

    @GetMapping("/user")
    public ModelClass modelClass(
            @ModelAttributeMeta(path = "src/test/resources/parameters_meta.json")
            @ModelAttribute
            ModelClass model){

        return model;

    }

}
