package org.example.projectjava.Jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.projectjava.Model.Member.Member;
import org.example.projectjava.Model.Member.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private MemberService memberService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = null;
        System.out.println("[path] " + request.getRequestURI());
        String path = request.getRequestURI();
        if (path.startsWith("/login") ||
                path.startsWith("/register") ||
                path.startsWith("/logout") ||
                path.startsWith("/NotAuthorizedPage") ||
                path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/images/") ||
                path.startsWith("/favicon.ico") ||
                path.startsWith("/.well-known/")) {
            filterChain.doFilter(request, response);
            return;
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token != null) {
            String email = jwtService.extractUsername(token);

            if (email != null) {
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    Optional<Member> memberOptional = memberService.findByEmail(email);
                    System.out.println("Member found for email: " + email);
                    if (memberOptional.isPresent()) {
                        Member member = memberOptional.get();
                        if (jwtService.isTokenValid(token, member.getEmail())) {
                            System.out.println("JWT Token is valid for email: " + email);
                            JwtAuthenticationToken authentication = new JwtAuthenticationToken(member, null, member.getAuthorities());
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    } else {
                        System.out.println("Member not found for email: " + email);
                    }
                }
            } else {
                System.out.println("Member not found for email: " + email);
                System.out.println("JWT Token is invalid or expired");
            }
        } else {
            System.out.println("No JWT Token found in cookies");
        }
        filterChain.doFilter(request, response);
    }
}
