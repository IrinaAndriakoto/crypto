package com.mobile.crypto.service;

import java.util.Date;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;


// import com.mobile.crypto.entity.Transaction;
// import com.mobile.crypto.repository.TransactionRepository;

@Service
public class CryptoService {
    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Double> getCryptoPrices() {
        String url = "https://api.coingecko.com/api/v3/simple/price?ids=bitcoin,ethereum,litecoin&vs_currencies=eur";
        return restTemplate.getForObject(url, Map.class);
    }
}