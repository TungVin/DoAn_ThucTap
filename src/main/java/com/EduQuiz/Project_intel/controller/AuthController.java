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

    // Hiển thị trang auth + nhận redirect (nếu có)
    @GetMapping
    public String authPage(@RequestParam(value = "redirect", required = false) String redirect,
                           Model model) {
        model.addAttribute("redirect", redirect);
        return "auth";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String role,
            @RequestParam(value = "redirect", required = false) String redirect,
            Model model
    ) {
        String result = userService.register(name, email, password, role);

        if (!"SUCCESS".equals(result)) {
            model.addAttribute("error", result);
            model.addAttribute("redirect", redirect);
            return "auth";
        }

        model.addAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
        model.addAttribute("redirect", redirect);
        return "auth";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(value = "redirect", required = false) String redirect,
            HttpSession session,
            Model model
    ) {
        Optional<User> userOpt = userService.login(email, password);

        if (userOpt.isEmpty()) {
            model.addAttribute("error", "Email hoặc mật khẩu không đúng");
            model.addAttribute("redirect", redirect);
            return "auth";
        }

        User user = userOpt.get();
        session.setAttribute("user", user);

        // Teacher luôn về trang teacher
        if (user.getRole() == Role.TEACHER) {
            return "redirect:/teacher";
        }

        // Student: nếu có redirect hợp lệ thì quay lại link đó
        String safe = safeRedirect(redirect);
        if (safe != null) {
            return "redirect:" + safe;
        }

        return "redirect:/student";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth";
    }

    /**
     * Chỉ cho phép redirect nội bộ (bắt đầu bằng "/") để tránh open-redirect.
     */
    private String safeRedirect(String redirect) {
        if (redirect == null || redirect.isBlank()) return null;

        // chỉ nhận đường dẫn nội bộ
        if (!redirect.startsWith("/")) return null;

        // chặn kiểu http://, https://
        if (redirect.contains("://")) return null;

        // chặn ký tự xuống dòng
        if (redirect.contains("\r") || redirect.contains("\n")) return null;

        return redirect;
    }
}
