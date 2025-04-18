package com.example.dbs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dbs.model.StudentCouncil;

public interface StudentCouncilRepository extends JpaRepository<StudentCouncil, String> {
}
