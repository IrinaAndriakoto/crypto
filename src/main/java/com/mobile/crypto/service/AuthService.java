package com.mobile.crypto.service;

import com.mobile.crypto.dto.SignupRequest;
import com.mobile.crypto.dto.LoginRequest;
import com.mobile.crypto.dto.PinRequest;
import com.mobile.crypto.entity.User;
import com.mobile.crypto.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Transactional
    public void registerUser(SignupRequest request) {
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Un compte avec cet email existe déjà !");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(false);
        
        // Générer un token de validation unique
        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);

        userRepository.save(user);

        // Envoi de l'email d'activation
        sendVerificationEmail(user.getEmail(), verificationToken);
    }

    public void verifyUser(String token) {
        User user = userRepository.findByVerificationToken(token)
            .orElseThrow(() -> new RuntimeException("Lien de validation invalide !"));

        user.setEnabled(true);
        user.setVerificationToken(null); // Supprimer le token après vérification
        userRepository.save(user);
    }

    private void sendVerificationEmail(String email, String token) {
        String verificationUrl = "http://localhost:8088/api/auth/verify?token=" + token;
        String message = "Cliquez sur le lien suivant pour activer votre compte : " + verificationUrl;

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("Vérification de votre compte");
        mailMessage.setText(message);

        mailSender.send(mailMessage);
    }

    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Email ou mot de passe incorrect"));
    
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Email ou mot de passe incorrect");
        }
    
        String pin = String.format("%06d", new SecureRandom().nextInt(1000000));
        user.setPin(pin);
        userRepository.save(user);
    
        // TODO: Envoyer PIN par email
    
        return "PIN envoyé par email";
    }
    
    public String verifyPin(PinRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    
        if (!user.getPin().equals(request.getPin())) {
            throw new RuntimeException("PIN invalide");
        }
    
        // TODO: Générer et retourner un JWT ici
    
        return "Authentification réussie !";
    }
}
