package org.example.projectjava.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private Object principal;
    private String token;

    public JwtAuthenticationToken(Object principal, String token) {
        super(null);
        this.principal = principal;
        this.token = token;
        setAuthenticated(true);
    }

    public JwtAuthenticationToken(Object principal, String token, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.token = token;
        setAuthenticated(true);
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
