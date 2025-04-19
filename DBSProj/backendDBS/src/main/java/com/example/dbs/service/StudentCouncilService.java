package com.example.dbs.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dbs.model.StudentCouncil;
import com.example.dbs.repository.StudentCouncilRepository;

@Service
public class StudentCouncilService {

    @Autowired
    private StudentCouncilRepository studentCouncilRepository;

    public List<StudentCouncil> getAllCouncilMembers() {
        return studentCouncilRepository.findAll();
    }

    public Optional<StudentCouncil> getCouncilMemberByEmail(String email) {
        return studentCouncilRepository.findById(email);
    }

    public boolean existsByEmail(String email) {
        return studentCouncilRepository.existsById(email);
    }

    @Transactional
    public StudentCouncil saveCouncilMember(StudentCouncil member) {
        // Basic validation for required fields (inherited and specific)
        if (member.getEmail() == null || member.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty.");
        }
        if (member.getName() == null || member.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        if (member.getPhone() == null) {
             throw new IllegalArgumentException("Phone number cannot be null.");
        }
        if (member.getRegno() == null) {
            throw new IllegalArgumentException("Registration number (regno) cannot be null.");
        }
        if (member.getPosition() == null || member.getPosition().trim().isEmpty()) {
            throw new IllegalArgumentException("Position cannot be null or empty for Student Council members.");
        }

        return studentCouncilRepository.save(member);
    }

    @Transactional
    public void deleteCouncilMember(String email) {
        // Check if member exists
        if (!studentCouncilRepository.existsById(email)) {
             return;
        }
        studentCouncilRepository.deleteById(email);
    }
}
