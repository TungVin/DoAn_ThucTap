package com.EduQuiz.Project_intel.controller;

import com.EduQuiz.Project_intel.dto.ExamUpsertForm;
import com.EduQuiz.Project_intel.service.CategoryService;
import com.EduQuiz.Project_intel.service.ExamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/teacher/exams")
public class ExamController {

    private final ExamService examService;
    private final CategoryService categoryService;

    public ExamController(ExamService examService, CategoryService categoryService) {
        this.examService = examService;
        this.categoryService = categoryService;
    }

    @GetMapping("/new")
    public String newExam(Model model) {
        model.addAttribute("mode", "create");
        model.addAttribute("form", new ExamUpsertForm());
        model.addAttribute("categories", categoryService.getAll());
        return "teacher/exam-editor";
    }

    @PostMapping
    public String createExam(@ModelAttribute("form") ExamUpsertForm form) {
        Long id = examService.createFromForm(form);
        return "redirect:/teacher/exams/" + id + "/edit";
    }

    @GetMapping("/{id}/edit")
    public String editExam(@PathVariable Long id, Model model) {
        model.addAttribute("mode", "edit");
        model.addAttribute("examId", id);
        model.addAttribute("form", examService.getFormById(id));
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("shareLink", "/quiz/" + id);
        return "teacher/exam-editor";
    }

    @PostMapping("/{id}")
    public String updateExam(@PathVariable Long id, @ModelAttribute("form") ExamUpsertForm form) {
        examService.updateFromForm(id, form);
        return "redirect:/teacher/exams/" + id + "/edit";
    }
}
