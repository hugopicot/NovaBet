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
    public Long getId(){
        return id;
    }
    public void setId(Long id){}
    public String getUsername(){
        return username;
    }
    public void setUsername(String username){}
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){}

    public String getPasswordHash(){
        return passwordHash;
    }
    public void setPasswordHash(String passwordHash){}
    public String getCreatedAt(){
        return createdAt;
    }

    public void setCreatedAt(String createdAt){}

}
