package com.mobile.crypto.service;

import com.mobile.crypto.dto.SignupRequest;
import com.mobile.crypto.dto.LoginRequest;
import com.mobile.crypto.dto.PinRequest;
import com.mobile.crypto.entity.User;
import com.mobile.crypto.repository.UserRepository;
import com.mobile.crypto.config.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Date;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.BadCredentialsException;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.UUID;
import java.util.Collections;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jw;

    private final int maxAttempts = 3;


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
        System.out.println("Token avant sauvegarde : " + user.getVerificationToken());

        userRepository.save(user);

        User savedUser = userRepository.findByEmail(request.getEmail()).orElse(null);
    System.out.println("Token après sauvegarde : " + (savedUser != null ? savedUser.getVerificationToken() : "null"));
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
            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));

        if (user.isAccountLocked()) {
            throw new LockedException("Compte bloqué, veuillez réinitialiser votre tentative");        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            user.setLoginAttempts(user.getLoginAttempts() + 1);
            if (user.getLoginAttempts() >= maxAttempts) {
                user.setAccountLocked(true);
                sendUnlockEmail(user);
                throw new LockedException("Compte verrouillé");
            }
            userRepository.save(user);
            throw new RuntimeException("Email ou mot de passe incorrect");        }

        // 🔄 Réinitialisation du compteur après une connexion réussie
        String pin = String.format("%06d", new SecureRandom().nextInt(1000000));
        
        user.setPin(pin);
        user.setLoginAttempts(0);
        user.setAccountLocked(false);

        // Définir la date d'expiration (maintenant + 90 secondes)
        user.setPinExpiration(new Date(System.currentTimeMillis() + (90 * 1000)));

        userRepository.save(user);
        sendPinEmail(user.getEmail(), pin);

        return "PIN envoyé";

        // UserDetails userDetails = new org.springframework.security.core.userdetails.User(
        //     user.getEmail(),
        //     user.getPassword(),
        //     user.isEnabled(),
        //     true,  // account non-expired
        //     true,  // credentials non-expired
        //     !user.isAccountLocked(),  // account non-locked
        //     Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        // );
        
        // return jw.generateToken(userDetails);
    }
    
    private void sendUnlockEmail(User user) {
        String resetUrl = "http://localhost:8088/api/auth/reset-attempts?email=" + user.getEmail();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Réinitialisation des tentatives de connexion");
        message.setText("Votre compte est verrouillé. Cliquez ici pour le déverrouiller : " + resetUrl);
        mailSender.send(message);
    }

    

    private void sendPinEmail(String to, String pin) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Votre code PIN de connexion");
        message.setText("Votre code PIN est : " + pin + "\nIl est valable pendant 90 secondes.");
        mailSender.send(message);
    }
}
