package com.example.dbs.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dbs.model.FloorManager;

public interface FloorManagerRepository extends JpaRepository<FloorManager, String> {
    Optional<FloorManager> findByEmail(String email);
}
