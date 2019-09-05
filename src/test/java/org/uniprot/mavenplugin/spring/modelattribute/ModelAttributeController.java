package org.uniprot.mavenplugin.spring.modelattribute;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ModelAttributeController {
    @GetMapping("/user")
    public User updateUser(@ModelAttribute User user, long age){
        return user;
    }

}
