package com.mobile.crypto.repository;

import com.mobile.crypto.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
// import java

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserId(Long userId);
}