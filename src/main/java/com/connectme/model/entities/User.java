package com.connectme.model.entities;

import com.connectme.model.enums.AccessLevel;
import com.connectme.model.enums.UserStatus;

import java.time.LocalDateTime;

public class User {
    private int id;
    private String username;
    private String email;
    private String passwordHash;
    private AccessLevel role;
    private UserStatus status;
    private LocalDateTime createDate;
    private LocalDateTime lastLogin;

    public User() {}

    public User(int id, String username, String email, String passwordHash, AccessLevel role, UserStatus status, LocalDateTime createDate, LocalDateTime lastLogin) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.status = status;
        this.createDate = createDate;
        this.lastLogin = lastLogin;
    }

    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public AccessLevel getRole() {
        return role;
    }

    public void setRole(AccessLevel role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
}
