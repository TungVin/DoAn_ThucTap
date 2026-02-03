package com.EduQuiz.Project_intel.controller;

import com.EduQuiz.Project_intel.model.Role;
import com.EduQuiz.Project_intel.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/auth";
        model.addAttribute("user", user);
        model.addAttribute("backUrl", user.getRole() == Role.TEACHER ? "/teacher" : "/student");
        return "profile";
    }

    @GetMapping("/settings")
    public String settings(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/auth";
        model.addAttribute("user", user);
        model.addAttribute("backUrl", user.getRole() == Role.TEACHER ? "/teacher" : "/student");
        return "settings";
    }
}
