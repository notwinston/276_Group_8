package com.example.demo.models;

import com.example.demo.enums.RoleType;

import jakarta.persistence.*;

@Entity
@Table(name = "group_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING) 
    @Column(nullable = false)
    private RoleType role = RoleType.USER;

    public User() {}

    public User(String username, String email, String password, RoleType role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = (role == null) ? RoleType.USER : role;
    }

    public Long getUid() { return uid; }
    public void setUid(Long uid) { this.uid = uid; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public RoleType getRole() { return role; }
    public void setRole(RoleType role) { this.role = (role == null) ? RoleType.USER : role; }

}