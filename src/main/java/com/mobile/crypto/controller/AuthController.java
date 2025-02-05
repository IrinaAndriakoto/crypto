package com.mobile.crypto.controller;

import com.mobile.crypto.dto.SignupRequest;
import com.mobile.crypto.dto.LoginRequest;
import com.mobile.crypto.dto.PinRequest;
import com.mobile.crypto.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequest request) {
        authService.registerUser(request);
        return ResponseEntity.ok("Utilisateur enregistré. Vérifiez votre email pour activer votre compte.");
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        authService.verifyUser(token);
        return ResponseEntity.ok("Email vérifié avec succès !");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    
    @PostMapping("/verify-pin")
    public ResponseEntity<String> verifyPin(@RequestBody PinRequest request) {
        return ResponseEntity.ok(authService.verifyPin(request));
    }
}
