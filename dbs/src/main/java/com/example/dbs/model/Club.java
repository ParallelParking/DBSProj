package com.example.dbs.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Club {
    @Id
    private String name;
    private String email;
    private String facultyHead;
    public Club() {
    }
    public Club(String name, String email, String facultyHead) {
        this.name = name;
        this.email = email;
        this.facultyHead = facultyHead;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getFacultyHead() {
        return facultyHead;
    }
    public void setFacultyHead(String facultyHead) {
        this.facultyHead = facultyHead;
    }
    @Override
    public String toString() {
        return "Club [name=" + name + ", email=" + email + ", facultyHead=" + facultyHead + "]";
    }
    
}
