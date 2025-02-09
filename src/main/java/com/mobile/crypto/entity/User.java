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
    private String password;
    private boolean enabled = false; // Par défaut, l'utilisateur n'est pas activé
    private String pin;

    @Temporal(TemporalType.TIMESTAMP)  // 📌 Ajout du champ expiration
    private Date pinExpiration;

    private String verificationToken;


}
