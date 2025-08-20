package com.microtest.UserService.config;

import com.microtest.UserService.config.jwt.JwtRequestFilter;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private UserDetailsService userDetailsService;

    @Autowired
    private JwtRequestFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.csrf().disable()
//                        .authorizeHttpRequests()
//                                .requestMatchers("/api/payment/**").hasRole("ADMIN")
//                        .requestMatchers("/api/order/**").hasAnyRole("USER", "ADMIN")
//                        .anyRequest().authenticated()
//                        .and()
//                                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);


        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for simplicity
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/auth/**", "/actuator/**").permitAll() // Permit all access to /auth/welcome
                                .requestMatchers("/api/payment/**").hasRole("ADMIN")
                                .requestMatchers("/api/order/**").hasAnyRole("USER","ADMIN")
                                .anyRequest().authenticated() // Require authentication for /auth/user/**
                )
                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

}
