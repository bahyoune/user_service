package com.microtest.UserService.controller;


import com.microtest.UserService.config.jwt.JwtUtil;
import com.microtest.UserService.dto.AuthentificationRequest;
import com.microtest.UserService.dto.SignUpRequest;
import com.microtest.UserService.dto.SignupResponse;
import com.microtest.UserService.exception.LoginOrEmailExistException;
import com.microtest.UserService.service.AuthService;
import com.microtest.UserService.service.RefreshTokenStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    //<editor-fold defaultState="collapsed" desc="javax.persistence.OneToMany">
    //</editor-fold>

    @Autowired
    private JwtUtil jwt;

    @Autowired
    private RefreshTokenStore store;

    @Autowired
    private AuthService authService;

    @Autowired
    private  AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService userDetailsService;

    @Value("${jwt.access.expiration}")
    private Long jwtAccessSeconds;
    @Value("${jwt.refresh.expiration}")
    private Long jwtRefreshSeconds;


    // Demo authentication — replace with real user check (DB/IdP/etc.)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthentificationRequest auth)
            throws BadCredentialsException,
            DisabledException, UsernameNotFoundException {

        try {

            authenticationManager
                    .authenticate(new
                            UsernamePasswordAuthenticationToken(auth.getUsername().toLowerCase(),
                            auth.getPassword()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Bad Credentials"));
        } catch (DisabledException disabledException) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(Map.of("error", "Users Disable"));
        }

        final UserDetails userDetails = userDetailsService.
                loadUserByUsername(auth.getUsername().toLowerCase());

        // issue refresh (jti tracked), then access
        Instant refExp = Instant.now().plusSeconds(jwtRefreshSeconds);
        String jti = store.issue(auth.getUsername(), refExp);
        String refreshToken = jwt.generateRefreshToken(auth.getUsername(), jti);
        String accessToken = jwt.generateAccessToken(userDetails);

        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "tokenType", "Bearer",
                "expiresIn", jwtAccessSeconds
        ));

    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestParam(value = "refreshToken") String refreshToken) {
        if (!jwt.isRefreshValid(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "invalid_or_expired_refresh"));
        }
        String userId = jwt.subject_refresh(refreshToken);
        String jti = jwt.jti(refreshToken);

        // Rotation: only the latest jti per userId is valid; issue a new one and invalidate the old
        if (!store.isCurrent(userId, jti, Instant.now())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "refresh_rotated_or_revoked"));
        }

        // rotate
        Instant refExp = Instant.now().plusSeconds(jwtRefreshSeconds);
        String newJti = store.issue(userId, refExp);
        String newRefresh = jwt.generateRefreshToken(userId, newJti);

        // access token (role could be looked up again; here we reuse previous role or re-query)
        final UserDetails userDetails = userDetailsService.
                loadUserByUsername(userId);

        String newAccess = jwt.generateAccessToken(userDetails);

        return ResponseEntity.ok(Map.of(
                "accessToken", newAccess,
                "refreshToken", newRefresh,
                "tokenType", "Bearer",
                "expiresIn", jwtAccessSeconds
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam String user) {
        store.revoke(user); // invalidate current refresh token
        return ResponseEntity.ok(Map.of("status", "logged_out"));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @RequestBody(required = true) SignUpRequest dto) {

        try {
            SignupResponse res = authService.createUser(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        } catch (LoginOrEmailExistException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.LENGTH_REQUIRED).body(e.getMessage());
        }

    }


}