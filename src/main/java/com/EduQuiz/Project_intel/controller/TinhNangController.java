package com.EduQuiz.Project_intel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TinhNangController {

    @GetMapping("/TinhNang")
    public String TinhNang() {
        return "./ChucNang/TinhNang";  
    }
}