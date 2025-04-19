package com.example.dbs.model;

import jakarta.persistence.Entity;

@Entity
public class StudentCouncil extends Student {
    private String position;

    public StudentCouncil() {
    }

    public StudentCouncil(String email, String name, Long phone, Long regno, String position, String password) {
        super(email, name, phone, regno, password);
        this.position = position;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "StudentCouncil [position=" + position + ", getRegno()=" + getRegno() + ", getEmail()=" + getEmail()
                + ", getName()=" + getName() + ", getPhone()=" + getPhone() + "]";
    }
    
}
