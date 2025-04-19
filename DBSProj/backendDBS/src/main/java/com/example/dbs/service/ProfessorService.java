package com.example.dbs.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.dbs.model.Professor;
import com.example.dbs.repository.ProfessorRepository;

@Service
public class ProfessorService {

    @Autowired private ProfessorRepository professorRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    public List<Professor> getAllProfessors() {
        return professorRepository.findAll();
    }

    public Optional<Professor> getProfessorByEmail(String email) {
        return professorRepository.findById(email);
    }

    public Professor createProfessor(Professor professor) {
        professor.setPassword(passwordEncoder.encode(professor.getPassword()));
        return professorRepository.save(professor);
    }

    public void deleteProfessor(String email) {
        professorRepository.deleteById(email);
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public void updatePassword(String email, String newPassword) {
        Optional<Professor> professorOpt = professorRepository.findById(email);
        if (professorOpt.isPresent()) {
            Professor professor = professorOpt.get();
            professor.setPassword(passwordEncoder.encode(newPassword));
            professorRepository.save(professor);
        } else {
            throw new RuntimeException("Professor not found with email: " + email);
        }
    }
}
