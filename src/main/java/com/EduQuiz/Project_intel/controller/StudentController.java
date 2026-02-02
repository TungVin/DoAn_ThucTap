package com.EduQuiz.Project_intel.controller;

import com.EduQuiz.Project_intel.model.Role;
import com.EduQuiz.Project_intel.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student")
public class StudentController {

    @GetMapping
    public String studentPage(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/auth";
        if (user.getRole() != Role.STUDENT) return "redirect:/teacher";
        return "student";
    }
}
