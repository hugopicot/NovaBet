package com.polymarket.model;

public class users {
    private Long id;
    private String username;
    private String email;
    private String passwordHash;
    private String createdAt;

    public users(Long id, String username, String email, String passwordHash, String createdAt){
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    public users(String username, String email, String passwordHash, String createdAt) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    public users() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "users{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
