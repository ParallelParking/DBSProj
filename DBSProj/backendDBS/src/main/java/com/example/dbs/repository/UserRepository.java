package com.example.dbs.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dbs.model.Users;

public interface UserRepository extends JpaRepository<Users, String> {

    Optional<Users> findByEmail(String email);

}
