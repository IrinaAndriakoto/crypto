package com.mobile.crypto.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String fullName;
    private String phoneNumber;
    private String password;
    private boolean enabled = false; // Par défaut, l'utilisateur n'est pas activé
    private String pin;

    @Temporal(TemporalType.TIMESTAMP)  // 📌 Ajout du champ expiration
    private Date pinExpiration;

    @Column(name = "verification_token")
    private String verificationToken;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int loginAttempts = 0; // Nombre de tentatives de connexion

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean accountLocked = false; 

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Wallet wallet;

}
