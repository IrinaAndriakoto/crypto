package com.mobile.crypto.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mobile.crypto.entity.Transaction;
import com.mobile.crypto.repository.TransactionRepository;

// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    public void createTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByUserId(userId);
    }
}