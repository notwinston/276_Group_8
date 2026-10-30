package com.example.demo.model;
import java.time.LocalDateTime;
import java.io.Serializable;
import com.example.demo.enums.RoleType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

// This class is used to store a created user account information
@Entity
@Table(name="group_user")
public class User implements Serializable{
    private static final long serialVersionUID = 1L; 
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; 

    @Column(nullable = false)
    private String name; 

    @Column(unique = true, nullable = false)
    private String email; 

    @Column(name = "password", nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)  
    private RoleType role; 

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public User (){}

    public User(String name, 
                String email, 
                String password, 
                RoleType role, 
                LocalDateTime createdAt){
        this.name = name; 
        this.email = email; 
        this.password = password; 
        this.role = role; 
        this.createdAt = createdAt; 
    }

    public long getId() {return id;}
    public void setId(long id) {this.id = id;}
    
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    
    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
    
    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}
    
    public RoleType getRole() {return role;}
    public void setRole(RoleType role) {this.role = role;}
    
    public LocalDateTime getCreatedAt() {return createdAt;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}
}
