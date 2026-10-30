package com.example.demo.controller;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.enums.RoleType;
import com.example.demo.model.User;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.UserRepository;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
public class AuthController {

    private final StudentRepository studentRepository; 
    private final UserRepository userRepository; 
    private final AdminRepository adminRepository; 

    // private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public AuthController (StudentRepository studentRepository, 
                           UserRepository userRepository, 
                           AdminRepository adminRepository){
        this.studentRepository = studentRepository; 
        this.userRepository = userRepository; 
        this.adminRepository = adminRepository; 
    }

    // Root URL
    @GetMapping("/")
    public String home(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if(loggedInUser == null) {
            return "redirect:/login";
        }
        return redirectByRole(loggedInUser);
    }

    // Show Login Page
    @GetMapping("/login")
    public String showLoginPage(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if(loggedInUser != null) {
            return redirectByRole(loggedInUser);
        }
        return "login";
    }

    // Process Login Form
    @PostMapping("/login")
    public String processLogin(
        @RequestParam String email,
        @RequestParam String password,
        HttpSession session,
        HttpServletResponse response,
        Model model
    ) {
        Optional<User> userOpt = userRepository.findByEmail(email.trim().toLowerCase());

        if(userOpt.isEmpty() || !userOpt.get().getPassword().equals(password)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); //401 for test cases
            model.addAttribute("error", "Invalid email or passoword.");
            return "login";
        }

        User user = userOpt.get();
        session.setAttribute("loggedInUser", user);

        return redirectByRole(user);
    }

    // Show Registration Page
    @GetMapping("/register")
    public String showRegisterPage(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if(loggedInUser != null) {
            return redirectByRole(loggedInUser);
        }
        return "register";
    }

    // Handle Registration Form Submission
    @PostMapping("/register")
    public String processRegister(
        @RequestParam String name,
        @RequestParam String email,
        @RequestParam String password,
        @RequestParam(defaultValue = "USER") RoleType role,
        HttpServletResponse response,
        Model model
    ) {
        String normalizedEmail = email.trim().toLowerCase();

        if(userRepository.existsByEmail(normalizedEmail)) {
            response.setStatus(HttpServletResponse.SC_CONFLICT); // 409 for duplicate email test case
            model.addAttribute("error", "An account with this email already exists.");
            return "register";
        }

        User newUser = new User(
            name.trim(),
            normalizedEmail,
            password,
            role,
            LocalDateTime.now()
        );
        userRepository.save(newUser);

        response.setStatus(HttpServletResponse.SC_CREATED);
        model.addAttribute("success", "Account created! Please log in.");
        return "login";
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout=true";
    }

    // Student View
    @GetMapping("/student/dashboard")
    public String studentDashboard(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("loggedInUser");
        if(currentUser == null) {
            return "redirect:/login";
        }
        else if(currentUser.getRole() == RoleType.ADMIN) {
            return "redirect:/admin/dashboard";
        }
        else {
            model.addAttribute("user", currentUser);
            model.addAttribute("students", studentRepository.findAll());
            return "student-dashboard";
        }
    }

    // Admin View
    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, HttpServletResponse response, Model model) {
        User currentUser = (User) session.getAttribute("loggedInUser");
        if(currentUser == null) {
            return "redirect:/login";
        }
        else if(currentUser.getRole() != RoleType.ADMIN) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "redirect:/student/dashboard?error=unauthorized";
        }
        else {
            model.addAttribute("user", currentUser);
            model.addAttribute("allUsers", userRepository.findAll());
            model.addAttribute("allStudents", studentRepository.findAll());
            model.addAttribute("allAdmins", adminRepository.findAll());
            return "admin-dashboard";
        }
    }

    // Admin (Delete a User)
    @PostMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable long id, HttpSession session) {
        User currentUser = (User) session.getAttribute("loggedInUser");
        if(currentUser == null || currentUser.getRole() != RoleType.ADMIN) {
            return "redirect:/login";
        }
        else if(currentUser.getId() != id){
            userRepository.deleteById(id);
        }
        return "redirect:/admin/dashboard";
    } 

    // Helper: Route user based on RoleType
    private String redirectByRole(User user) {
        if(user.getRole() == RoleType.ADMIN) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/user/dashboard";
    }

    
}
