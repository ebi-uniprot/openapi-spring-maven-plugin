package org.uniprot.mavenplugin.spring.modelattribwithmeta;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ModelAttributeWithMetaController {

    @GetMapping("/user")
    public ModelClass modelClass(
            @ModelAttribute
                    ModelClass model) {

        return model;

    }

}
