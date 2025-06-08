package org.example.projectjava.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.projectjava.model.Member;
import org.example.projectjava.service.MemberService;
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
        String path = request.getRequestURI();
        if (path.startsWith("/loginController") ||
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
                    if (memberOptional.isPresent()) {
                        Member member = memberOptional.get();
                        if (jwtService.isTokenValid(token, member.getEmail())) {
                            JwtAuthenticationToken authentication = new JwtAuthenticationToken(member, null, member.getAuthorities());
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    }
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
