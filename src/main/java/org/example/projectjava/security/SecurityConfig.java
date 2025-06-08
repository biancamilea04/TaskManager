package org.example.projectjava.security;

import org.example.projectjava.jwt.JwtAuthenticationFilter;
import org.example.projectjava.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private MemberService memberService;

    @Autowired
    private UnauthorizedHandler unauthorizedHandler;

    @Bean
    public UserDetailsService userDetailsService() {
        return memberService;
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(memberService);
        provider.setPasswordEncoder(encoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtTokenAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(unauthorizedHandler)
                )
                .formLogin(
                        httpForm ->
                                httpForm
                                        .loginPage("/login").permitAll()
                                        .loginProcessingUrl("/doLogin")
                )
                .authorizeHttpRequests(
                        authorizeRequests ->
                            authorizeRequests
                                    .requestMatchers("/login", "/register","/not-authorized", "/css/**", "/js/**","/images/**", "/favicon.ico", "/.well-known/**").permitAll()
                                    .anyRequest().authenticated()

                )
                .addFilterBefore(jwtTokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
