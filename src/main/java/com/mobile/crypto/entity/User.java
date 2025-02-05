package com.mobile.crypto.entity;

import lombok.Data;
import javax.persistence.*;

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
    private String verificationToken;
}
