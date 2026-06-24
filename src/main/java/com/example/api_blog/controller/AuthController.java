package com.example.api_blog.controller;

import com.example.api_blog.model.request.LoginRequest;
import com.example.api_blog.model.request.RegisterRequest;
import com.example.api_blog.model.response.ApiResponse;
import com.example.api_blog.model.response.LoginResponse;
import com.example.api_blog.model.response.UserResponse;
import com.example.api_blog.model.entity.Auth;
import com.example.api_blog.service.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auths")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        Auth auth = authService.register(registerRequest);
        // FIXED: return UserResponse, not Auth (which contains password hash)
        UserResponse userResponse = new UserResponse(auth.getUserId(), auth.getUserName(), auth.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>("Registered successfully", userResponse, HttpStatus.CREATED.value(), LocalDateTime.now())
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(
                new ApiResponse<>("Login successfully", response, HttpStatus.OK.value(), LocalDateTime.now())
        );
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout-all")
    public ResponseEntity<ApiResponse<String>> logoutAll() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getName().equals("anonymousUser")) {
            throw new RuntimeException("User not authenticated");
        }

        String email = authentication.getName();
        authService.logoutAll(email);

        return ResponseEntity.ok(
                new ApiResponse<>("Logged out from all devices successfully", null, 200, LocalDateTime.now())
        );
    }
}
