package org.example.projectjava.Controller;

import org.example.projectjava.Com.RegisterRequest;
import org.example.projectjava.Model.MyUser;
import org.example.projectjava.Model.MyUserRepository;
import org.example.projectjava.Model.MyUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegistrationController {

    @Autowired
    private MyUserRepository myUserRepository;
    @Autowired
    private MyUserService myUserService;

    @GetMapping("/register")
    public String registerPage() {
        return "registerPage";
    }

    @PostMapping("/register")
    public ResponseEntity<MyUser> createUser(@RequestBody RegisterRequest user) {
        MyUser myUser = new MyUser();
        myUser.setUsername(user.username);
        myUser.setPassword(user.password);
        myUser.setEmail(user.email);

        try {
            MyUser savedUser = myUserRepository.save(myUser);
//            myUserRepository.flush();
            return ResponseEntity.ok(savedUser); // HTTP 200 OK with user info
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // HTTP 500
        }
    }
}
