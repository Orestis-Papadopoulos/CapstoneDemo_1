package com.example.application.backend.entity;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Defines the User table to be created in the database.
 * @Id refers to the primary key.
 * */

@Entity
public class User {
    @Id
    @Column(name = "user_uuid", nullable = false)
    private String user_uuid;

    @Pattern(regexp = "^[a-zA-Z]*$", message = "Must contain only letters.")
    @Size(min = 2, max = 30, message = "Must be 2 to 30 characters long.")
    private String first_name;
    @Pattern(regexp = "^[a-zA-Z]*$", message = "Must contain only letters.")
    @Size(min = 2, max = 30, message = "Must be 2 to 30 characters long.")
    private String last_name;
    private String proximity_card_id;
    private String sign_in_session_uuid;

    // see: https://vaadin.com/blog/saving-and-displaying-images-using-jpa
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] photo;

    // constructor to create User object based on a uuid
    public User(String user_uuid) {
        this.user_uuid = user_uuid;
    }

    // needs a default constructor
    public User() {

    }

    @Override
    public String toString() {
        return "First name: " + this.first_name;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getSign_in_session_uuid() {
        return sign_in_session_uuid;
    }

    public void setSign_in_session_uuid(String sign_in_session_uuid) {
        this.sign_in_session_uuid = sign_in_session_uuid;
    }

    public String getProximity_card_id() {
        return proximity_card_id;
    }

    public void setProximity_card_id(String proximity_card_id) {
        this.proximity_card_id = proximity_card_id;
    }

    public String getUser_uuid() {
        return user_uuid;
    }

    public void setUser_uuid(String user_uuid) {
        this.user_uuid = user_uuid;
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
