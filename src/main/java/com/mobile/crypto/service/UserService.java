package com.mobile.crypto.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mobile.crypto.entity.User;
import com.mobile.crypto.repository.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class UserService {
    private final UserRepository userrepo;


    @Autowired
    public UserService(UserRepository userrep){
        this.userrepo = userrep;
    }

    public List<User> getAllUsers(){
        return userrepo.findAll();
    }
    
    public User getUserById(Long id){
        return userrepo.findById(id).orElse(null);
    }

    // public boolean isValidLogin(String nom, String password) {
    //     User utilisateur = userrepo.findByNom(nom);

    //     return utilisateur != null && utilisateur.getPassword().equals(password);
    // }
    // public User createUtilisateur(User u) {
    //     return userrepo.save(u);
    // }
}

