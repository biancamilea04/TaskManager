package org.example.projectjava.controller.loginController;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.example.projectjava.DTO.LoginDTO;
import org.example.projectjava.model.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Key;
import java.util.Date;

@RestController
public class LoginRestController {
    @Autowired
    private AuthenticationManager authenticationManager;

    final String SECRET_KEY = "d8fc877cb52bfe727e8c93940947cd853f628d3e5a204648953b3535bc547c430dfdb7ee2e8bb8449eb033f0cb77fb51633c089edc3a7f3569eaf5449dc5c2a1595185167d84c8b711defb70672d8f857f000bedfb5b8cf120db65b55a1c2ff2781ea0ddc06600c77548755e96deb6826cf6608bcfed96a0af56639bef3bdd5bfe347345c824ff224733f0cb7c83af34ed353e0fdf4dec5174e554a696bff7434b5ebe236a1f4f967230dd0b4889ef4da951fb08c3e322b03e42076a4eb17d39877d6d4c97a04bfed72e040684ecbfdd8d1a35a8022ab1489ba1fbf9f63f108ebb18e6510295f717f2a199a362378388dcaecfa7d71dc53862abf784b65970c39a6d02d6b0a26c7f89a02826382de9a6d14d67c977f7d6fe68233dd86514718bcdd12f8693f87fbb401965c067fd23ebe174ff37e75deaa1ce7334cd2922de2c0c933c9251ead59479aa6ef9bbc6a642fca3ca9f1e9cdd4e45cc3a143bba6e34a6966727a59758d589a895be7c0a15139ae0ded8dc14e1819325649384627ee62b5fcb2451b3ba0402b42ac1b87000ebc7514c1cb364ebe391c0e6e42ec41cd1963b2ff0c66c6b4494e7011c809830e281f19e0b1328b8f0f21a3f930749c811a4810a54ad182187cd75ae5c4094909941d4b55be2ca7dd0fc2c3b3bae94c79bb5eb396f32d64313696952d7afee3f203f6de31592c4a8d03c8ab08aa1c5ce9e";

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO user, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.email, user.password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            Member member = (Member) authentication.getPrincipal();

            byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
            Key key = Keys.hmacShaKeyFor(keyBytes);
            String jwt = Jwts.builder()
                    .setSubject(member.getEmail())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();

            Cookie cookie = new Cookie("user", user.email);
            cookie.setPath("/");
            response.addCookie(cookie);

            Cookie jwtCookie = new Cookie("jwt", jwt);
            jwtCookie.setPath("/");
            response.addCookie(jwtCookie);

            var authorities = member.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            for (var authority : authorities) {
                System.out.println(authority);
                Cookie roleCookie = new Cookie("role", authority);
                roleCookie.setPath("/");
                response.addCookie(roleCookie);
            }

            return ResponseEntity.ok("{\"jwt\":\"" + jwt + "\"}");

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }

}
