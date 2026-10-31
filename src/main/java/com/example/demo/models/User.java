package com.example.demo.models;

import jakarta.persistence.*;

@Entity 
@Table(name = "group_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    public User() {}

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

    public Long getUid() { return uid; }
    public void setUid(Long uid) { this.uid = uid; }

    public String getUsername() { return username; }
    public void setUsername(String username) {this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
}
