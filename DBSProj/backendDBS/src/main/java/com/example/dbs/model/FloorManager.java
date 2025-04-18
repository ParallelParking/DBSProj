package com.example.dbs.model;

import jakarta.persistence.Entity;

@Entity
public class FloorManager extends User{

    public FloorManager(){
    }
 
    public FloorManager(String email, String name, Long phone) {
        super(email, name, phone);
    }
 
    @Override
    public String toString() {
        return "FloorManager [getEmail()=" + getEmail() + ", getName()=" + getName() + ", getPhone()=" + getPhone() + "]";
    }
}
