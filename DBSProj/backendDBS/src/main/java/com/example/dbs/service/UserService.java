package com.example.dbs.service;

import com.example.dbs.model.User;
import com.example.dbs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findById(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(String email) {
        userRepository.deleteById(email);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsById(email);
    }
}
