package com.microtest.UserService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microtest.UserService.config.jwt.JwtUtil;
import com.microtest.UserService.dto.SignUpRequest;
import com.microtest.UserService.dto.SignupResponse;
import com.microtest.UserService.exception.LoginOrEmailExistException;
import com.microtest.UserService.service.AuthService;
import com.microtest.UserService.service.RefreshTokenStore;
import com.microtest.UserService.support.TestEventFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(
        properties = "spring.cloud.config.enabled=false"
)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;
    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private RefreshTokenStore refreshTokenStore;
    @MockitoBean
    private AuthenticationManager authenticationManager;
    @MockitoBean
    private UserDetailsService userDetailsService;

    private SignUpRequest request;

    @BeforeEach
    void setup() {
        request = TestEventFactory.signUpRequest();
    }

    @Test
    public void testCreateUser_valid_user() throws Exception {
        //GIVEN
        SignupResponse response = TestEventFactory.signupResponse();

        Mockito.when(authService.createUser(Mockito.any(SignUpRequest.class)))
                .thenReturn(response);

        //WHEN + THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").exists());

        Mockito.verify(authService, Mockito.timeout(1)).createUser(Mockito.any(SignUpRequest.class));
    }

    @Test
    public void testCreateUser_email_or_login_exist() throws Exception {
        //GIVEN
        Mockito.when(authService.createUser(Mockito.any(SignUpRequest.class))).thenThrow(LoginOrEmailExistException.class);

        //WHEN + THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
                )
                .andExpect(MockMvcResultMatchers.status().isConflict());

        Mockito.verify(authService, Mockito.timeout(1))
                .createUser(Mockito.any(SignUpRequest.class));

    }

    @Test
    public void testCreateUser_password_error() throws Exception {
        //GIVEN
        Mockito.when(authService.createUser(Mockito.any(SignUpRequest.class))).thenThrow(IllegalArgumentException.class);

        //WHEN + THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
                )
                .andExpect(MockMvcResultMatchers.status().isLengthRequired());

        Mockito.verify(authService, Mockito.timeout(1))
                .createUser(Mockito.any(SignUpRequest.class));

    }

}
