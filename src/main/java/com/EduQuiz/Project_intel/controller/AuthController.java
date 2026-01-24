package com.EduQuiz.Project_intel.controller;

import com.EduQuiz.Project_intel.dto.RegisterRequest;
import com.EduQuiz.Project_intel.model.User;
import com.EduQuiz.Project_intel.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterRequest request,
                           RedirectAttributes redirectAttributes,
                           HttpSession session) {
        try {
            // Kiểm tra các trường bắt buộc
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng nhập họ và tên");
                return "redirect:/auth";
            }

            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng nhập email");
                return "redirect:/auth";
            }

            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng nhập mật khẩu");
                return "redirect:/auth";
            }

            if (request.getRole() == null || request.getRole().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng chọn vai trò");
                return "redirect:/auth";
            }

            if (request.getAcceptTerms() == null || !request.getAcceptTerms()) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng chấp nhận điều khoản");
                return "redirect:/auth";
            }

            User user = authService.register(request);

            // Lưu thông tin user vào session
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userName", user.getName());
            session.setAttribute("userRole", user.getRole().toString());

            redirectAttributes.addFlashAttribute("success",
                "Đăng ký thành công! Chào mừng " + user.getName());

            // Chuyển hướng dựa trên vai trò
            if (user.getRole() == User.Role.TEACHER) {
                return "redirect:/teacher";
            } else {
                return "redirect:/student";
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth";
        }
    }

    @PostMapping("/login")
    public String login(String email,
                        String password,
                        RedirectAttributes redirectAttributes,
                        HttpSession session) {
        try {
            if (email == null || email.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng nhập email");
                return "redirect:/auth";
            }

            if (password == null || password.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng nhập mật khẩu");
                return "redirect:/auth";
            }

            User user = authService.login(email, password);

            // Lưu thông tin user vào session
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userName", user.getName());
            session.setAttribute("userRole", user.getRole().toString());

            redirectAttributes.addFlashAttribute("success",
                "Đăng nhập thành công! Chào mừng " + user.getName());

            // Chuyển hướng dựa trên vai trò
            if (user.getRole() == User.Role.TEACHER) {
                return "redirect:/teacher";
            } else {
                return "redirect:/student";
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "Đã đăng xuất thành công");
        return "redirect:/";
    }
}

