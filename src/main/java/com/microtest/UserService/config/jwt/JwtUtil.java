package com.microtest.UserService.config.jwt;



import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    @Value("${spring.security.oauth2.resourceserver.jwt.secret-key}")
    private String accessSecret;
    @Value("${jwt.refresh.secret}")
    private String refreshSecret;
    @Value("${jwt.access.expiration}")
    private Long accessExp;
    @Value("${jwt.refresh.expiration}")
    private Long refreshExp;


    //<editor-fold defaultState="collapsed" desc="javax.persistence.OneToMany">
    //</editor-fold>

    //<editor-fold defaultState="collapsed" desc="UserId + JTI + Role">
    public String subject_access(String token) {
        return parseAccess(token).getBody().getSubject();
    }

    public String subject_refresh(String token) {
        return parseRefresh(token).getBody().getSubject();
    }

    public String role(String token) {
        return parseAccess(token).getBody().get("role", String.class);
    }

    public String jti(String token) {
        return parseRefresh(token).getBody().getId();
    }
    //</editor-fold>

    //<editor-fold defaultState="collapsed" desc="Is Valid jwt">
    public boolean isRefreshValid(String jwt) {
        return isValid(jwt, false);
    }

    private boolean isValid(String jwt, boolean access) {
        try {
            if (access) parseAccess(jwt);
            else parseRefresh(jwt);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
    //</editor-fold>

    //<editor-fold defaultState="collapsed" desc="Claims">
    public Jws<Claims> parseAccess(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey_access())
                .build()
                .parseClaimsJws(jwt);
    }

    public Jws<Claims> parseRefresh(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey_refresh())
                .build()
                .parseClaimsJws(jwt);
    }
    //</editor-fold>

    //<editor-fold defaultState="collapsed" desc="GenerateToken">
    public String generateAccessToken(String userId, String role) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(userId)
                .claim("roles", List.of("ROLE_" + role))
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessExp * 1000))
                .signWith(getSignKey_access(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String userId, String tokenId) {
        Date now = new Date();
        // tokenId (jti) enables rotation/revocation checks
        return Jwts.builder()
                .setSubject(userId)
                .setId(tokenId)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshExp * 1000))
                .signWith(getSignKey_refresh(), SignatureAlgorithm.HS256)
                .compact();
    }
    //</editor-fold>


    //<editor-fold defaultState="collapsed" desc="Key Secret">
    private SecretKey getSignKey_access() {
        return new SecretKeySpec(
                accessSecret.getBytes(),
                "HmacSHA256"
        );

    }

    private SecretKey getSignKey_refresh() {
        return new SecretKeySpec(
                refreshSecret.getBytes(),
                "HmacSHA256"
        );
    }
    //</editor-fold>

}
