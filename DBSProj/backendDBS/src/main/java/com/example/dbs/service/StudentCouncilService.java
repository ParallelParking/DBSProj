package com.example.dbs.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.dbs.model.StudentCouncil;
import com.example.dbs.repository.StudentCouncilRepository;

@Service
public class StudentCouncilService {

    @Autowired private StudentCouncilRepository studentCouncilRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    public List<StudentCouncil> getAllCouncilMembers() {
        return studentCouncilRepository.findAll();
    }

    public Optional<StudentCouncil> getCouncilMemberByEmail(String email) {
        return studentCouncilRepository.findById(email);
    }

    public StudentCouncil createCouncilMember(StudentCouncil member) {
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        return studentCouncilRepository.save(member);
    }

    public void deleteCouncilMember(String email) {
        studentCouncilRepository.deleteById(email);
    }

    public void updatePassword(String email, String newPassword) {
        Optional<StudentCouncil> memberOptional = studentCouncilRepository.findById(email);
        if (memberOptional.isPresent()) {
            StudentCouncil member = memberOptional.get();
            member.setPassword(passwordEncoder.encode(newPassword));
            studentCouncilRepository.save(member);
        } else {
            throw new RuntimeException("SC member not found with email: " + email);
        }
    }
}
