package com.EduQuiz.Project_intel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index"; // trả về file index.html trong templates

    }
    @GetMapping("/auth") // Thêm phương thức này
    public String auth() {
        return "auth"; // Trả về file auth.html trong templates

    }
}
