package com.example.dbs.service;

import com.example.dbs.model.Professor;
import com.example.dbs.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfessorService {

    @Autowired
    private ProfessorRepository professorRepository;

    public List<Professor> getAllProfessors() {
        return professorRepository.findAll();
    }

    public Optional<Professor> getProfessorByEmail(String email) {
        return professorRepository.findById(email);
    }

    public Professor createProfessor(Professor professor) {
        return professorRepository.save(professor);
    }

    public void deleteProfessor(String email) {
        professorRepository.deleteById(email);
    }
}
