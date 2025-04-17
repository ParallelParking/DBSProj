package com.example.dbs.repository;

import com.example.dbs.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorRepository extends JpaRepository<Professor, String> {
}
