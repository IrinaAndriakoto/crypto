package com.mobile.crypto.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "wallet")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double balance; // Solde en euros
    private double bitcoin;
    private double ethereum;
    private double litecoin;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Getters et Setters
}