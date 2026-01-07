package com.microtest.UserService.config;


import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;


import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
public class SecurityBeans {

    @Value("${spring.security.oauth2.resourceserver.jwt.secret-key}")
    private String secret;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private SecretKey secretKey() {
        return new SecretKeySpec(
                secret.getBytes(),
                "HmacSHA256"
        );
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey()));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder
                .withSecretKey(secretKey())
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}
