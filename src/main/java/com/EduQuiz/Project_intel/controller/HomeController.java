package com.EduQuiz.Project_intel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index"; // trả về file index.html trong templates
    }

    @GetMapping("/auth")
    public String auth() {
        return "auth"; // Trả về file auth.html trong templates
    }

    @GetMapping("/student")
    public String student() {
        return "student"; // Trả về file student.html trong templates
    }

    @GetMapping("/HTT")
    public String ChonHinhThuc() {
        return "ChonHinhThuc"; // Trả về file ChonHinhThuc.html trong templates
    }

}
