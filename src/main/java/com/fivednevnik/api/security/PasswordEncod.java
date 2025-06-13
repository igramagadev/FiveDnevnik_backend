package com.fivednevnik.api.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component

public class PasswordEncod implements PasswordEncoder {
    
    private final BCryptPasswordEncoder bCryptEncoder;
    
    public PasswordEncod() {
        this.bCryptEncoder = new BCryptPasswordEncoder();
    }
    
    @Override
    public String encode(CharSequence rawPassword) {
        return "{bcrypt}" + bCryptEncoder.encode(rawPassword);
    }
    
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {

        
        if (encodedPassword == null) {
            return false;
        }
        if (encodedPassword.startsWith("{bcrypt}")) {
            String actualEncodedPassword = encodedPassword.substring(8);
            return bCryptEncoder.matches(rawPassword, actualEncodedPassword);
        }

        if (encodedPassword.startsWith("$2a$") || encodedPassword.startsWith("$2b$") || encodedPassword.startsWith("$2y$")) {
            return bCryptEncoder.matches(rawPassword, encodedPassword);
        }

        return rawPassword.equals(encodedPassword);
    }
} 
