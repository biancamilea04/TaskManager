package org.example.projectjava.Controller;

import org.example.projectjava.Com.LoginRequest;
import org.example.projectjava.Model.MyUser;
import org.example.projectjava.Model.MyUserRepository;
import org.example.projectjava.Model.MyUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class LoginRegisterController {

    @Autowired
    private MyUserService myUserService;

    @GetMapping("/login")
    public String loginPage() {
        return "loginPage";
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest user) {
        Optional<MyUser> userOpt = myUserService.authenticate(user.username, user.password);

        System.out.println(user.username + " " + user.password);

        if (userOpt.isPresent()) {
            return ResponseEntity.ok("Login successful. Redirect to /home");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }
}
