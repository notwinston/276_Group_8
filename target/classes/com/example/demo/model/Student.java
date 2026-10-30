import java.io.Serializable;

import jakarta.annotation.Generated;

@Entity
@Table(name="student")
public class Student implements Serializable{
    private static final long serialVersionUID = 1L; 
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; 
    @Column(nullable = false)
    private User user;
    @Column(nullable = false)
    private String program; 

    public Student (){}

    public Student (User user, String program){
        this.user = user; 
        this.program = program; 
    }

    public void setUser(User user){this.user = user; }
    public User getUser(){return user;}

    public void setProgram(String program){this.program = program;}
    public String getProgram(){return program;}
}
