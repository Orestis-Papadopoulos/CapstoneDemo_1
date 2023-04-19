package com.example.application.backend.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Defines the Account table to be created in the database.
 * @Id refers to the primary key.
 * */

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "account_id", nullable = false)
    private Long account_id;

    @NotNull
    private String user_uuid;
    @Size(min = 1, message = "Cannot be empty.")
    private String username;
    @Size(min = 1, message = "Cannot be empty.")
    private String password;
    @Size(min = 1, message = "Cannot be empty.")
    private String login_page_url;
    @Size(min = 1, message = "Cannot be empty.")
    private String username_css_selector;
    @Size(min = 1, message = "Cannot be empty.")
    private String password_css_selector;
    private String btn_cookies_css_selector;
    @Size(min = 1, message = "Cannot be empty.")
    private String btn_login_css_selector;
    @Pattern(regexp = "^[a-zA-Z]*$", message = "Must contain only letters.")
    @Size(min = 1, max = 30, message = "Must be 1 to 30 characters long.")
    private String account_name;

    @Size(max = 50, message = "Must be max 50 characters")
    private String comment;
    private String date_modified;

    // default constructor
    public Account() {

    }

    // assign an account to another account
    public Account(Account account) {
        this.account_name = account.getAccount_name();
        this.comment = account.getComment();
        this.login_page_url = account.getLogin_page_url();
        this.username_css_selector = account.getUsername_css_selector();
        this.password_css_selector = account.getPassword_css_selector();
        this.btn_cookies_css_selector = account.getBtn_cookies_css_selector();
        this.btn_login_css_selector = account.getBtn_login_css_selector();
    }

    // setters and getters
    public Long getAccount_id() {
        return account_id;
    }

    public void setAccount_id(Long account_id) {
        this.account_id = account_id;
    }

    public String getUser_uuid() {
        return user_uuid;
    }

    public void setUser_uuid(String user_uuid) {
        this.user_uuid = user_uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBtn_cookies_css_selector() {
        return btn_cookies_css_selector;
    }

    public void setBtn_cookies_css_selector(String btn_cookies_css_selector) {
        this.btn_cookies_css_selector = btn_cookies_css_selector;
    }

    public String getLogin_page_url() {
        return login_page_url;
    }

    public void setLogin_page_url(String login_page_url) {
        this.login_page_url = login_page_url;
    }

    public String getUsername_css_selector() {
        return username_css_selector;
    }

    public void setUsername_css_selector(String username_css_selector) {
        this.username_css_selector = username_css_selector;
    }

    public String getPassword_css_selector() {
        return password_css_selector;
    }

    public void setPassword_css_selector(String password_css_selector) {
        this.password_css_selector = password_css_selector;
    }

    public String getBtn_login_css_selector() {
        return btn_login_css_selector;
    }

    public void setBtn_login_css_selector(String btn_login_css_selector) {
        this.btn_login_css_selector = btn_login_css_selector;
    }

    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate_modified() {
        return date_modified;
    }

    public void setDate_modified(String date_modified) {
        this.date_modified = date_modified;
    }
}
