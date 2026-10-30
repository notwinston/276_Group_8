package com.example.demo.controller;
import org.springframework.stereotype.Controller;

import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.UserRepository;

// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
public class AuthController {

    private final StudentRepository studentRepoitory; 
    private final UserRepository userRepository; 
    private final AdminRepository adminRepository; 

    // private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public AuthController (StudentRepository studentRepository, 
                           UserRepository userRepository, 
                           AdminRepository adminRepository){
        this.studentRepoitory = studentRepository; 
        this.userRepository = userRepository; 
        this.adminRepository = adminRepository; 
    }


    
}
