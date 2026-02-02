package com.EduQuiz.Project_intel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }


    @GetMapping("/HTT")
    public String chonHinhThuc() {
        return "ChonHinhThuc";
    }
}
