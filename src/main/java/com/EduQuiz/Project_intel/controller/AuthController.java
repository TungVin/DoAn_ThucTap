package com.EduQuiz.Project_intel.controller;

import com.EduQuiz.Project_intel.model.Role;
import com.EduQuiz.Project_intel.model.User;
import com.EduQuiz.Project_intel.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String authPage() {
        return "auth";
    }

    // ===================== REGISTER =====================
    @PostMapping("/register")
    public String register(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String role,
            Model model
    ) {
        String result = userService.register(name, email, password, role);

        if (!"SUCCESS".equals(result)) {
            model.addAttribute("error", result);
            return "auth";
        }

        model.addAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
        return "auth";
    }

    // ===================== LOGIN =====================
    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model
    ) {
        Optional<User> userOpt = userService.login(email, password);

        if (userOpt.isEmpty()) {
            model.addAttribute("error", "Email hoặc mật khẩu không đúng");
            return "auth";
        }

        User user = userOpt.get();
        session.setAttribute("user", user);

        if (user.getRole() == Role.TEACHER) {
            return "redirect:/teacher";
        }

        return "redirect:/student";
    }

    // ===================== LOGOUT =====================
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth";
    }
}
