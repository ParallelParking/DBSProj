package com.example.dbs.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.dbs.model.Security;
import com.example.dbs.repository.SecurityRepository;

@Service
public class SecurityService {

    @Autowired private SecurityRepository securityRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    public List<Security> getAllSecurity() {
        return securityRepository.findAll();
    }

    public Optional<Security> getSecurityByEmail(String email) {
        return securityRepository.findById(email);
    }

    public Security createSecurity(Security security) {
        security.setPassword(passwordEncoder.encode(security.getPassword()));
        return securityRepository.save(security);
    }

    public void deleteSecurity(String email) {
        securityRepository.deleteById(email);
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public void updatePassword(String email, String newPassword) {
        Optional<Security> securityOpt = securityRepository.findById(email);
        if (securityOpt.isPresent()) {
            Security security = securityOpt.get();
            security.setPassword(passwordEncoder.encode(newPassword));
            securityRepository.save(security);
        } else {
            throw new RuntimeException("Security not found with email: " + email);
        }
    }
}
