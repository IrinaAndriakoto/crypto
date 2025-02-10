package com.mobile.crypto.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mobile.crypto.entity.Wallet;
import com.mobile.crypto.repository.WalletRepository;

@Service
public class WalletService {
    @Autowired
    private WalletRepository walletRepository;

    public Wallet getWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("Portefeuille introuvable"));
    }

    public void updateWallet(Wallet wallet) {
        walletRepository.save(wallet);
    }
}