package com.example.api_blog.service.impl;

import com.example.api_blog.jwt.JwtService;
import com.example.api_blog.model.entity.Auth;
import com.example.api_blog.model.request.LoginRequest;
import com.example.api_blog.model.request.RegisterRequest;
import com.example.api_blog.model.response.LoginResponse;
import com.example.api_blog.repository.AuthRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private AuthRepo authRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    private Auth auth;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        auth = new Auth();
        auth.setUserId(1L);
        auth.setUserName("testuser");
        auth.setEmail("test@example.com");
        auth.setPassword("encodedPassword");
        auth.setTokenVersion(0);

        registerRequest = new RegisterRequest();
        registerRequest.setUserName("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
    }

    @Test
    void testRegisterSuccess() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(authRepo.register(any(Auth.class))).thenReturn(auth);

        Auth registeredAuth = authService.register(registerRequest);

        assertNotNull(registeredAuth);
        assertEquals(auth.getEmail(), registeredAuth.getEmail());
        verify(passwordEncoder, times(1)).encode("password123");
        verify(authRepo, times(1)).register(any(Auth.class));
    }

    @Test
    void testLoginSuccess() {
        when(authRepo.findByEmail("test@example.com")).thenReturn(auth);
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken(auth)).thenReturn("test-jwt-token");

        LoginResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("test-jwt-token", response.getToken());
        verify(authRepo, times(1)).findByEmail("test@example.com");
        verify(passwordEncoder, times(1)).matches("password123", "encodedPassword");
        verify(jwtService, times(1)).generateToken(auth);
    }

    @Test
    void testLoginUserNotFound() {
        when(authRepo.findByEmail("notfound@example.com")).thenReturn(null);
        loginRequest.setEmail("notfound@example.com");

        assertThrows(UsernameNotFoundException.class, () -> authService.login(loginRequest));
        verify(authRepo, times(1)).findByEmail("notfound@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void testLoginInvalidPassword() {
        when(authRepo.findByEmail("test@example.com")).thenReturn(auth);
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);
        loginRequest.setPassword("wrongpassword");

        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
        verify(authRepo, times(1)).findByEmail("test@example.com");
        verify(passwordEncoder, times(1)).matches("wrongpassword", "encodedPassword");
    }

    @Test
    void testLogoutAllSuccess() {
        when(authRepo.findByEmail("test@example.com")).thenReturn(auth);
        when(authRepo.incrementTokenVersion(1L)).thenReturn(1);

        int result = authService.logoutAll("test@example.com");

        assertEquals(1, result);
        verify(authRepo, times(1)).findByEmail("test@example.com");
        verify(authRepo, times(1)).incrementTokenVersion(1L);
    }

    @Test
    void testLogoutAllUserNotFound() {
        when(authRepo.findByEmail("notfound@example.com")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> authService.logoutAll("notfound@example.com"));
        verify(authRepo, times(1)).findByEmail("notfound@example.com");
        verify(authRepo, never()).incrementTokenVersion(anyLong());
    }
}
