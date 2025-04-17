package com.example.dbs.model;

import jakarta.persistence.Entity;

@Entity
public class Security extends User{

   public Security(){
   }

    public Security(String email, String name, Long phone) {
        super(email, name, phone);
    }

    @Override
    public String toString() {
        return "Security [getEmail()=" + getEmail() + ", getName()=" + getName() + ", getPhone()=" + getPhone() + "]";
    }
    
}
