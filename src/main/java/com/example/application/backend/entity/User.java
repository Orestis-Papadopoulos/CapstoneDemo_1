package com.example.application.backend.entity;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class User {
    @Id
    @Column(name = "id", nullable = false)
    private UUID uuid =  UUID.randomUUID();

    private String first_name;
    private String last_name;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }
}
