package com.example.dbs.service;

import com.example.dbs.model.StudentCouncil;
import com.example.dbs.repository.StudentCouncilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public StudentCouncil createCouncilMember(StudentCouncil member) {
        return studentCouncilRepository.save(member);
    }

    public void deleteCouncilMember(String email) {
        studentCouncilRepository.deleteById(email);
    }
}
