package org.example.projectjava.Security;

import lombok.AllArgsConstructor;
import org.example.projectjava.Model.MyUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private final MyUserService myUserService;

    @Bean
    public UserDetailsService userDetailsService() {
        return myUserService;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(myUserService);
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(
                      httpForm ->
                      httpForm
                              .loginPage("/login").permitAll()
                )
                .authorizeHttpRequests(
                        authorizeRequests -> {
                            authorizeRequests.requestMatchers("/login","/register","/css/**","/js/**").permitAll();
                            authorizeRequests.anyRequest().authenticated();
                        }
                ).build();
    }
}
