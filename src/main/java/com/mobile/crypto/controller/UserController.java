package com.mobile.crypto.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.mobile.crypto.config.JwtUtil;
import com.mobile.crypto.entity.*;
import com.mobile.crypto.repository.UserRepository;
import com.mobile.crypto.service.*;
import com.mobile.crypto.dto.UpdateUserRequest;

import java.security.Principal;

@RestController
@RequestMapping("/api/user")
// @CrossOrigin
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtils;
    // private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserController(UserService userService, JwtUtil jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User utilisateur = userService.getUserById(id);
        if (utilisateur != null) {
            return new ResponseEntity<>(utilisateur, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<User>> getAllUtilisateurs() {
        List<User> u = userService.getAllUsers();
        if(u!=null && !u.isEmpty()) {
            return new ResponseEntity<>(u,HttpStatus.OK);
        } else{
            return new ResponseEntity<>(Collections.emptyList(),HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserRequest request, Principal principal) {
        userService.updateUser(principal.getName(), request);
        return ResponseEntity.ok("Informations mises à jour avec succès.");
    }
   
}
