package org.example.projectjava.Controller;

import org.example.projectjava.Model.MyUser;
import org.example.projectjava.Model.MyUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {

    @Autowired
    private MyUserRepository myUserRepository;

    @GetMapping("/register")
    public String loginPage() {
        return "registerPage";
    }

    @PostMapping(value="/register",consumes = "application/json")
    public MyUser createUser(@RequestBody MyUser user){
           System.out.println(user.getUsername());
           return myUserRepository.save(user);
    }
}
