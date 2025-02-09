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

import java.security.SecureRandom;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jw;


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

        // Définir la date d'expiration (maintenant + 90 secondes)
        user.setPinExpiration(new Date(System.currentTimeMillis() + (90 * 1000)));

        userRepository.save(user);
    
        // Envoyer le PIN par email
        sendPinEmail(user.getEmail(), pin);
    
        return "PIN envoyé par email";
    }
    

    // public String verifyPin(PinRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
    //     User user = userRepository.findByEmail(request.getEmail())
    //         .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    
    //     if (!user.isEnabled()) {
    //         throw new RuntimeException("Le compte n'est pas activé");
    //     }
    
    //     System.out.println("PIN enregistré en base : " + user.getPin());
    //     System.out.println("PIN reçu : " + request.getPin());
    
    //     // 🔄 Vérifier l'expiration du PIN
    //     if (user.getPinExpiration() == null || user.getPinExpiration().before(new Date())) {
    //         throw new RuntimeException("PIN expiré. Veuillez vous reconnecter.");
    //     }
    
    //     if (!user.getPin().equals(request.getPin())) {
    //         throw new RuntimeException("PIN invalide");
    //     }
    
    //     // ✅ Authentification dans Spring Security
    //     UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
    //     UsernamePasswordAuthenticationToken authenticationToken =
    //             new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    
    //     SecurityContext context = SecurityContextHolder.createEmptyContext();
    //     context.setAuthentication(authenticationToken);
    //     SecurityContextHolder.setContext(context);
    
    //     // 🔐 Persister l'authentification dans la session pour les requêtes suivantes
    //     SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    //     securityContextRepository.saveContext(context, httpRequest, httpResponse);
    
    //     // 🔥 Générer le JWT
    //     String token = jw.generateToken(userDetails);
    //     System.out.println("JWT généré : " + token);
    
    //     // 🔄 Supprimer le PIN après validation
    //     user.setPin(null);
    //     user.setPinExpiration(null);
    //     userRepository.save(user);
            
    //     return token;
    // }
    

    

    private void sendPinEmail(String to, String pin) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Votre code PIN de connexion");
        message.setText("Votre code PIN est : " + pin + "\nIl est valable pendant 90 secondes.");
        mailSender.send(message);
    }
}
