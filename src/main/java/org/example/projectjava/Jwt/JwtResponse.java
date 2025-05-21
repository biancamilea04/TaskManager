package org.example.projectjava.Jwt;

import java.util.List;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String email;
    private List<String> roles;

    public JwtResponse(String accessToken, String email, List<String> roles) {
        this.token = accessToken;
        this.email = email;
        this.roles = roles;
    }
}
