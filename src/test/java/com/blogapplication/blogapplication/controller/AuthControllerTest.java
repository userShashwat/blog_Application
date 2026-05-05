package com.blogapplication.blogapplication.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.blogapplication.blogapplication.config.SecurityConfig;
import com.blogapplication.blogapplication.exception.BlogAPIException;
import com.blogapplication.blogapplication.payload.LoginDTO;
import com.blogapplication.blogapplication.payload.RegisterDTO;
import com.blogapplication.blogapplication.security.JwtAuthenticationEntryPoint;
import com.blogapplication.blogapplication.security.JwtAuthenticationFilter;
import com.blogapplication.blogapplication.security.JwtTokenProvider;
import com.blogapplication.blogapplication.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtAuthenticationEntryPoint.class})
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private AuthService authService;

    private LoginDTO loginDTO;
    private RegisterDTO registerDTO;

    @BeforeEach
    void setUp() {
        loginDTO = new LoginDTO();
        loginDTO.setUsernameOrEmail("johndoe");
        loginDTO.setPassword("password123");

        registerDTO = new RegisterDTO();
        registerDTO.setName("John Doe");
        registerDTO.setUsername("johndoe");
        registerDTO.setEmail("john@example.com");
        registerDTO.setPassword("password123");
    }

    @Test
    void login_Success() throws Exception {
        when(authService.login(any(LoginDTO.class)))
                .thenReturn("test.jwt.token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("test.jwt.token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void login_ViaSigninEndpoint_Success() throws Exception {
        when(authService.login(any(LoginDTO.class)))
                .thenReturn("test.jwt.token");

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("test.jwt.token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void login_InvalidCredentials_ReturnsBadRequest() throws Exception {
        when(authService.login(any(LoginDTO.class)))
                .thenThrow(new BlogAPIException(HttpStatus.BAD_REQUEST, "Invalid username or password"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    void login_MissingBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_EmptyUsernameOrEmail_ReturnsBadRequest() throws Exception {
        loginDTO.setUsernameOrEmail("");

        when(authService.login(any(LoginDTO.class)))
                .thenThrow(new BlogAPIException(HttpStatus.BAD_REQUEST, "Invalid username or password"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_Success() throws Exception {
        when(authService.register(any(RegisterDTO.class)))
                .thenReturn("Registration Successful");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Registration Successful"));
    }

    @Test
    void register_ViaSignupEndpoint_Success() throws Exception {
        when(authService.register(any(RegisterDTO.class)))
                .thenReturn("Registration Successful");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Registration Successful"));
    }

    @Test
    void register_DuplicateUsername_ReturnsBadRequest() throws Exception {
        when(authService.register(any(RegisterDTO.class)))
                .thenThrow(new BlogAPIException(HttpStatus.BAD_REQUEST, "Username already exists"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username already exists"));
    }

    @Test
    void register_DuplicateEmail_ReturnsBadRequest() throws Exception {
        when(authService.register(any(RegisterDTO.class)))
                .thenThrow(new BlogAPIException(HttpStatus.BAD_REQUEST, "Email already exists"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }

    @Test
    void register_MissingBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}