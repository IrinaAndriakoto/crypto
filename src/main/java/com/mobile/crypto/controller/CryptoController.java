package com.mobile.crypto.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mobile.crypto.service.CryptoService;
import java.util.Map;

@RestController
@RequestMapping("/api/crypto")
@Tag(name = "Crypto", description = "Endpoints pour la crypto")
public class CryptoController {
    @Autowired
    private CryptoService cryptoService;

    @GetMapping("/prices")
    public Map<String, Double> getPrices() {
        return cryptoService.getCryptoPrices();
    }
}