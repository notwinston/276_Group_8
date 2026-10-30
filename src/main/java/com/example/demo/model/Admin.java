package com.example.demo.model;
import java.io.Serializable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity 
@Table(name = "admin")
public class Admin implements Serializable{
    private static final long serialVersionUID = 1l; 
    
    // Creates a column named user_id in the admin table.
    // It is a foreign key that stores the id of the User (row in users)
    // this student belongs to. unique = true means one User can have
    // at most one Student profile.
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; 

    @OneToOne(optional = false)
    @JoinColumn (name = "user_id", nullable = false)
    private User user; 

    public Admin (){}

    public Admin (User user){
        this.user = user;
    }

    public void setUser(User user){this.user = user;}
    public User getUser(){
        return user; 
    }
}
