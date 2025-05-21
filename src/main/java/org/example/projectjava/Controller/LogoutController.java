package org.example.projectjava.Controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogoutController {
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        Cookie cookieJwt = new Cookie("jwt", null);
        cookieJwt.setMaxAge(0);
        cookieJwt.setPath("/");
        cookieJwt.setHttpOnly(true);
        cookieJwt.setSecure(true);

        Cookie cookieUser = new Cookie("user", null);
        cookieUser.setMaxAge(0);
        cookieUser.setPath("/");
        cookieUser.setHttpOnly(true);
        cookieUser.setSecure(true);

        Cookie cookieRole = new Cookie("role", null);
        cookieRole.setMaxAge(0);
        cookieRole.setPath("/");
        cookieRole.setHttpOnly(true);
        cookieRole.setSecure(true);

        response.addCookie(cookieJwt);
        response.addCookie(cookieUser);
        response.addCookie(cookieRole);

        return ResponseEntity.ok("Logged out successfully");
    }
}
