package com.example.dbs.service;

import com.example.dbs.model.Security;
import com.example.dbs.repository.SecurityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SecurityService {

    @Autowired
    private SecurityRepository securityRepository;

    public List<Security> getAllSecurity() {
        return securityRepository.findAll();
    }

    public Optional<Security> getSecurityByEmail(String email) {
        return securityRepository.findById(email);
    }

    public Security createSecurity(Security security) {
        return securityRepository.save(security);
    }

    public void deleteSecurity(String email) {
        securityRepository.deleteById(email);
    }
}
