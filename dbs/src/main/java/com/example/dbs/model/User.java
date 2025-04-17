package com.example.dbs.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
    @Id
    private String email;
    private String name;
    private Long phone;

    public User(){
    }
    public User(String email, String name, Long phone) {
        this.email = email;
        this.name = name;
        this.phone = phone;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Long getPhone() {
        return phone;
    }
    public void setPhone(Long phone) {
        this.phone = phone;
    }
    @Override
    public String toString() {
        return "User [email=" + email + ", name=" + name + ", phone=" + phone + "]";
    }
    
}
