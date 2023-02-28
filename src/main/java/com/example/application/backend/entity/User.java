package com.example.application.backend.entity;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class User {
    @Id
    @Column(name = "uuid", nullable = false)
    private String uuid;

    private String first_name;
    private String last_name;
    private String proximity_card_id;
    private String session_id;

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public String getProximity_card_id() {
        return proximity_card_id;
    }

    public void setProximity_card_id(String proximity_card_id) {
        this.proximity_card_id = proximity_card_id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
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
