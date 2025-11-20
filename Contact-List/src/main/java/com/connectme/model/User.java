package com.connectme.model;

public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String status;
    private int failedAttempts;

    public User(int id, String username, String passwordHash, String status, int failedAttempts) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.status = status;
        this.failedAttempts = failedAttempts;
    }

    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getStatus() { return status; }
    public int getFailedAttempts() { return failedAttempts; }
}
