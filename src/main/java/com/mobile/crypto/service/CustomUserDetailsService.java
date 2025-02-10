package com.mobile.crypto.service;

import com.mobile.crypto.entity.User;
import com.mobile.crypto.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.LockedException;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
            
            if (user.isAccountLocked()) {
                throw new LockedException("Votre compte est verrouillé en raison de plusieurs échecs de connexion.");
            }

        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(), // Make sure this field exists in your User entity
            user.isEnabled(),
            true, // account non-expired
            true, // credentials non-expired
            !user.isAccountLocked(), // account non-locked
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}