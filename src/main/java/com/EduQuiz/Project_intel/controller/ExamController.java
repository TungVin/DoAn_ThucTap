package com.EduQuiz.Project_intel.controller;

import com.EduQuiz.Project_intel.dto.ExamUpsertForm;
import com.EduQuiz.Project_intel.service.CategoryService;
import com.EduQuiz.Project_intel.service.ExamService;
import com.EduQuiz.Project_intel.service.ExamQuestionItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@Controller
@RequestMapping("/teacher/exams")
public class ExamController {

    private final ExamService examService;
    private final CategoryService categoryService;
    private final ExamQuestionItemService examQuestionItemService;

    public ExamController(ExamService examService,
                          CategoryService categoryService,
                          ExamQuestionItemService examQuestionItemService) {
        this.examService = examService;
        this.categoryService = categoryService;
        this.examQuestionItemService = examQuestionItemService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("exams", examService.getAll());
        return "teacher/exams";
    }

    @GetMapping("/new")
    public String newExam(Model model) {
        model.addAttribute("mode", "create");
        model.addAttribute("form", new ExamUpsertForm());
        model.addAttribute("categories", categoryService.getAll());
        return "teacher/exam-editor";
    }

    
    @PostMapping
    public String createExam(@ModelAttribute("form") ExamUpsertForm form,
                             @RequestParam(value = "questionsJson", required = false) String questionsJson,
                             RedirectAttributes ra) {
        try {
            Long examId = examService.createFromForm(form);

            if (examId != null) {
                examQuestionItemService.replaceFromJson(examId, questionsJson);
            }

            ra.addFlashAttribute("toast", "Tạo bài kiểm tra thành công!");
            return "redirect:/teacher?activeTab=exams";
        } catch (Exception e) {
            ra.addFlashAttribute("examError", "Tạo bài kiểm tra thất bại: " + e.getMessage());
            return "redirect:/teacher/exams/new";
        }
    }

    @GetMapping("/{id}/edit")
public String editExam(@PathVariable("id") Long id, Model model) {
    model.addAttribute("mode", "edit");
    model.addAttribute("examId", id);
    model.addAttribute("form", examService.getFormById(id));
    model.addAttribute("categories", categoryService.getAll());

    String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
            .build()
            .toUriString();

    model.addAttribute("shareLink", baseUrl + "/quiz/" + id);

    model.addAttribute("questionsJson", examQuestionItemService.getQuestionsJsonByExamId(id));


    return "teacher/exam-editor";
}

 
    @PostMapping("/{id}")
    public String updateExam(@PathVariable Long id,
                             @ModelAttribute("form") ExamUpsertForm form,
                             @RequestParam(value = "questionsJson", required = false) String questionsJson,
                             RedirectAttributes ra) {
        if (id == null) {
            ra.addFlashAttribute("examError", "ID bài kiểm tra không hợp lệ.");
            return "redirect:/teacher?activeTab=exams";
        }

        try {
            examService.updateFromForm(id, form);

            examQuestionItemService.replaceFromJson(id, questionsJson);

            ra.addFlashAttribute("toast", "Lưu thay đổi thành công!");
            return "redirect:/teacher?activeTab=exams";
        } catch (Exception e) {
            ra.addFlashAttribute("examError", "Lưu thất bại: " + e.getMessage());
            return "redirect:/teacher?activeTab=exams";

        }
    }

  
    @PostMapping("/delete")
public String deleteExam(@RequestParam("id") Long id, RedirectAttributes ra) {
    try {
        examService.deleteById(id);
        ra.addFlashAttribute("toast", "Đã xóa bài kiểm tra!");
    } catch (Exception e) {
        ra.addFlashAttribute("examError", "Xóa thất bại: " + e.getMessage());
    }
    return "redirect:/teacher?activeTab=exams";
}

}
