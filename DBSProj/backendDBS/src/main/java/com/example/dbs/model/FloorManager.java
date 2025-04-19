package com.example.dbs.model;

import jakarta.persistence.Entity;

@Entity
public class FloorManager extends Users{

    public FloorManager(){
    }
 
    public FloorManager(String email, String name, Long phone, String password) {
        super(email, name, phone, password);
    }
 
    @Override
    public String toString() {
        return "FloorManager [getEmail()=" + getEmail() + ", getName()=" + getName() + ", getPhone()=" + getPhone() + "]";
    }
}
