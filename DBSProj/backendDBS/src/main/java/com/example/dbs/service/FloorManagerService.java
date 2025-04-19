package com.example.dbs.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.dbs.model.FloorManager;
import com.example.dbs.repository.FloorManagerRepository;

@Service
public class FloorManagerService {

    @Autowired private FloorManagerRepository floorManagerRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    public List<FloorManager> getAllFloorManagers() {
        return floorManagerRepository.findAll();
    }

    public Optional<FloorManager> getFloorManagerByEmail(String email) {
        return floorManagerRepository.findById(email);
    }

    public FloorManager createFloorManager(FloorManager manager) {
        manager.setPassword(passwordEncoder.encode(manager.getPassword()));
        return floorManagerRepository.save(manager);
    }

    public void deleteFloorManager(String email) {
        floorManagerRepository.deleteById(email);
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public void updatePassword(String email, String newPassword) {
        Optional<FloorManager> managerOpt = floorManagerRepository.findById(email);
        if (managerOpt.isPresent()) {
            FloorManager manager = managerOpt.get();
            manager.setPassword(passwordEncoder.encode(newPassword));
            floorManagerRepository.save(manager);
        } else {
            throw new RuntimeException("FloorManager not found with email: " + email);
        }
    }    
}
