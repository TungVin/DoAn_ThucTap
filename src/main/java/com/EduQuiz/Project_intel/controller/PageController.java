package com.EduQuiz.Project_intel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/policy")
    public String policyPage() {
        return "policy";
    }

    @GetMapping("/news")
    public String newsPage() {
        return "news";
    }
}

