package org.example.projectjava.controller;

import org.example.projectjava.model.Member;
import org.example.projectjava.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Controller
public class HomeController {
    @Autowired
    private MemberService memberService;

    @Autowired
    private NotificationController notificationController;

    @GetMapping("/home")
    public String homePage() {

        return "home/homePage";
    }


    @GetMapping("/api/current-username")
    @ResponseBody
    public ResponseEntity<String> getCurrentUsername(
            @CookieValue(value = "user", defaultValue = "")
            String email
    ) {
        if (email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Neautentificat");
        }

        Optional<Member> userOptional = memberService.findByEmail(email);
        if (userOptional.isPresent()) {
            Member user = userOptional.get();
            return ResponseEntity.status(HttpStatus.OK).body(user.getName());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilizatorul nu a fost gasit");
        }
    }
}
