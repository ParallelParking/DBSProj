package com.example.dbs.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Equipment {
    @Id
    private String type;

    public Equipment() {
    }

    public Equipment(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Equipment [type=" + type + "]";
    }
    
}
