package com.example.demo.controllers;

import com.example.demo.enums.RoleType;
import com.example.demo.models.User;
import com.example.demo.models.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String root(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user != null) {
            return (user.getRole() == RoleType.ADMIN) ? "redirect:/admin/dashboard" : "redirect:/dashboard";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    // Automatically detects role and sends ADMIN -> /admin/dashboard, USER -> /dashboard
    @PostMapping("/login")
    public String loginUser(@RequestParam String username,
                            @RequestParam String password,
                            HttpSession session,
                            Model model) {

        User user = userRepository.findByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("loggedInUser", user);

            if (user.getRole() == RoleType.ADMIN) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/dashboard";
            }
        }

        model.addAttribute("error", "Invalid username or password.");
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password,
                               Model model) {

        if (userRepository.existsByUsername(username)) {
            model.addAttribute("error", "Username is already taken.");
            return "register";
        }

        if (userRepository.existsByEmail(email)) {
            model.addAttribute("error", "Email is already registered.");
            return "register";
        }

        // Public registrations are always standard USER accounts
        User newUser = new User(username, email, password, RoleType.USER);
        userRepository.save(newUser);
        return "redirect:/login?registered=true";
    }

    // USER VIEW
    @GetMapping("/dashboard")
    public String showUserDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "dashboard";
    }

    // USER FEATURE: Update Email / Password
    @PostMapping("/dashboard/update-profile")
    public String updateProfile(@RequestParam String email,
                                @RequestParam(required = false) String newPassword,
                                HttpSession session,
                                Model model) {
        User sessionUser = (User) session.getAttribute("loggedInUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }

        User dbUser = userRepository.findByUsername(sessionUser.getUsername());
        if (!dbUser.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            model.addAttribute("error", "Email is already in use by another account.");
            model.addAttribute("user", dbUser);
            return "dashboard";
        }

        dbUser.setEmail(email);
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            dbUser.setPassword(newPassword);
        }
        userRepository.save(dbUser);
        session.setAttribute("loggedInUser", dbUser);

        model.addAttribute("success", "Profile updated successfully!");
        model.addAttribute("user", dbUser);
        return "dashboard";
    }

    // ADMIN VIEW (Protected: Standard users cannot access this!)
    @GetMapping("/admin/dashboard")
    public String showAdminDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }
        if (user.getRole() != RoleType.ADMIN) {
            return "redirect:/dashboard"; // Kick non-admins back to user dashboard
        }
        model.addAttribute("user", user);
        model.addAttribute("users", userRepository.findAll());
        return "admin_dashboard";
    }

    // ADMIN FEATURE 1: Toggle User Role (Promote USER to ADMIN or Demote ADMIN to USER)
    @PostMapping("/admin/users/{uid}/toggle-role")
    public String toggleUserRole(@PathVariable Long uid, HttpSession session) {
        User admin = (User) session.getAttribute("loggedInUser");
        if (admin == null || admin.getRole() != RoleType.ADMIN) {
            return "redirect:/login";
        }

        userRepository.findById(uid).ifPresent(targetUser -> {
            // Prevent admin from accidentally demoting themselves
            if (!targetUser.getUid().equals(admin.getUid())) {
                RoleType nextRole = (targetUser.getRole() == RoleType.ADMIN) ? RoleType.USER : RoleType.ADMIN;
                targetUser.setRole(nextRole);
                userRepository.save(targetUser);
            }
        });
        return "redirect:/admin/dashboard";
    }

    // ADMIN FEATURE 2: Delete User Account
    @PostMapping("/admin/users/{uid}/delete")
    public String deleteUser(@PathVariable Long uid, HttpSession session) {
        User admin = (User) session.getAttribute("loggedInUser");
        if (admin == null || admin.getRole() != RoleType.ADMIN) {
            return "redirect:/login";
        }

        // Prevent admin from deleting their own logged-in account
        if (!admin.getUid().equals(uid)) {
            userRepository.deleteById(uid);
        }
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}