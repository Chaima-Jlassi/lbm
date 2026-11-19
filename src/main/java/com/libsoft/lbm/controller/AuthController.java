package com.libsoft.lbm.controller;

import com.libsoft.lbm.dto.AuthResponse;
import com.libsoft.lbm.dto.LoginRequest;
import com.libsoft.lbm.dto.RegisterRequest;
import com.libsoft.lbm.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService service;

    public AuthController(AuthService service) { this.service = service; }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(r));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest r) { return service.login(r); }
}
