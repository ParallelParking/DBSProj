package com.example.dbs.service;

import com.example.dbs.model.FloorManager;
import com.example.dbs.repository.FloorManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FloorManagerService {

    @Autowired
    private FloorManagerRepository floorManagerRepository;

    public List<FloorManager> getAllFloorManagers() {
        return floorManagerRepository.findAll();
    }

    public Optional<FloorManager> getFloorManagerByEmail(String email) {
        return floorManagerRepository.findById(email);
    }

    public FloorManager createFloorManager(FloorManager manager) {
        return floorManagerRepository.save(manager);
    }

    public void deleteFloorManager(String email) {
        floorManagerRepository.deleteById(email);
    }
}
