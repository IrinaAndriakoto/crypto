package com.mobile.crypto.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import com.mobile.crypto.service.TransactionService;
import com.mobile.crypto.entity.Transaction;

import java.util.List;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @GetMapping("/{userId}")
    public List<Transaction> getTransactions(@PathVariable Long userId) {
        return transactionService.getTransactionsByUserId(userId);
    }
}