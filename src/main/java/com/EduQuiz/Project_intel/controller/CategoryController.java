package com.EduQuiz.Project_intel.controller;

import com.EduQuiz.Project_intel.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/teacher/categories")
    public String addCategory(@RequestParam String name,
                              @RequestParam(required = false) String description) {
        categoryService.create(name, description);
        return "redirect:/teacher#categories";
    }
}

