package com.EduQuiz.Project_intel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LienHeController {

    @GetMapping("/LienHe")
    public String lienHe() {
        return "./ChucNang/LienHe";  // Trả về tên file LienHe.html
    }
}