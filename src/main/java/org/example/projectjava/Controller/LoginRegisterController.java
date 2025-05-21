package org.example.projectjava.Controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.example.projectjava.Com.LoginRequest;
import org.example.projectjava.Model.Member.Member;
import org.example.projectjava.Model.Member.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Controller
public class LoginRegisterController {

    @Autowired
    private MemberService memberService;

    @GetMapping("/login")
    public String loginPage() {
        return "loginPage";
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest user, HttpServletResponse response) {

        System.out.println(user.email + "* *" + user.password);

        ResponseEntity<String> validAuthenticate = memberService.isAuthenticated(user.email, user.password);
        if(validAuthenticate.getStatusCode().equals(HttpStatus.CONFLICT)) {
            return validAuthenticate;
        }

        Optional<Member> userOpt = memberService.authenticate(user.email, user.password);

        System.out.println(user.email + "* *" + user.password);

        if (userOpt.isPresent()) {
            Cookie cookie = new Cookie("user", user.email);
            cookie.setPath("/");
            response.addCookie(cookie);

            return ResponseEntity.ok("Login successful. Redirect to /home");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }
}
