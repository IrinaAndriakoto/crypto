package com.mobile.crypto.controller;

// import org.springframework.security.core.userdetails.User;
import com.mobile.crypto.config.*;

import com.mobile.crypto.repository.UserRepository;
import com.mobile.crypto.entity.User;

import com.mobile.crypto.repository.UserRepository;
import com.mobile.crypto.config.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import java.util.*;

import java.util.ArrayList;
import com.mobile.crypto.dto.SignupRequest;
import com.mobile.crypto.dto.LoginRequest;
import com.mobile.crypto.dto.PinRequest;
import com.mobile.crypto.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "Authorization")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;

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
    
    @PostMapping("/verifypin")
    public ResponseEntity<?> verifyPin(@RequestBody PinRequest request) {
        try {
            User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

            if (!user.isEnabled()) {
                throw new RuntimeException("Le compte n'est pas activé");
            }

            if (user.getPinExpiration() == null || user.getPinExpiration().before(new Date())) {
                throw new RuntimeException("PIN expiré. Veuillez vous reconnecter.");
            }

            if (!user.getPin().equals(request.getPin())) {
                throw new RuntimeException("PIN invalide");
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
            String token = jwtUtil.generateToken(userDetails);
            
            user.setPin(null);
            user.setPinExpiration(null);
            userRepository.save(user);

            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Collections.singletonMap("error", e.getMessage()));
        }
    }


    @GetMapping("/generate-token")
    public String generateToken() {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
        .withUsername("andriakotoharisonirina@gmail.com")
        .password("americandream03")
        .authorities(new ArrayList<>())
        .build();
        
        String token = jwtUtil.generateToken(userDetails);
        System.out.println("Token généré : " + token);
        return token;
    }
}
