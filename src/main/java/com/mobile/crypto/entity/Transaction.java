package com.mobile.crypto.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; // DEPOSIT, WITHDRAWAL, BUY, SELL
    private double amount;
    private String currency; // EUR, BTC, ETH, LTC
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Getters et Setters
}