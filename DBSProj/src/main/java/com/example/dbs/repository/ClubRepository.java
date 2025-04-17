package com.example.dbs.repository;

import com.example.dbs.model.Club;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubRepository extends JpaRepository<Club, String> {
}
