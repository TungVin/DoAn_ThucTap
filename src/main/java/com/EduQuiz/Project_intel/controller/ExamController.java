package com.EduQuiz.Project_intel.controller;

import com.EduQuiz.Project_intel.dto.ExamUpsertForm;
import com.EduQuiz.Project_intel.service.CategoryService;
import com.EduQuiz.Project_intel.service.ExamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/teacher/exams")
public class ExamController {

    private final ExamService examService;
    private final CategoryService categoryService;

    public ExamController(ExamService examService, CategoryService categoryService) {
        this.examService = examService;
        this.categoryService = categoryService;
    }

    /**
     * (Tuỳ chọn) Nếu bạn có trang list riêng /teacher/exams
     * Còn nếu list nằm trong /teacher?activeTab=exams thì bạn không cần gọi route này.
     */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("exams", examService.getAll());
        return "teacher/exams"; // nếu bạn có templates/teacher/exams.html
    }

    @GetMapping("/new")
    public String newExam(Model model) {
        model.addAttribute("mode", "create");
        model.addAttribute("form", new ExamUpsertForm());
        model.addAttribute("categories", categoryService.getAll());
        return "teacher/exam-editor";
    }

    /**
     * TẠO XONG -> QUAY VỀ DANH SÁCH (giống Ninequiz)
     */
    @PostMapping
    public String createExam(@ModelAttribute("form") ExamUpsertForm form,
                             RedirectAttributes ra) {
        examService.createFromForm(form);
        ra.addFlashAttribute("toast", "Tạo bài kiểm tra thành công!");
        return "redirect:/teacher?activeTab=exams";
        // nếu bạn dùng list riêng: return "redirect:/teacher/exams";
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

    /**
     * LƯU SỬA XONG -> QUAY VỀ DANH SÁCH
     */
    @PostMapping("/{id}")
    public String updateExam(@PathVariable Long id,
                             @ModelAttribute("form") ExamUpsertForm form,
                             RedirectAttributes ra) {
        examService.updateFromForm(id, form);
        ra.addFlashAttribute("toast", "Lưu thay đổi thành công!");
        return "redirect:/teacher?activeTab=exams";
        // nếu bạn muốn ở lại edit: return "redirect:/teacher/exams/" + id + "/edit";
    }

    /**
     * XÓA (modal đang submit POST /teacher/exams/delete)
     */
    @PostMapping("/delete")
    public String deleteExam(@RequestParam("id") Long id,
                             RedirectAttributes ra) {
        try {
            examService.deleteById(id); // bạn cần thêm method này trong ExamService
            ra.addFlashAttribute("toast", "Đã xóa bài kiểm tra!");
        } catch (Exception e) {
            ra.addFlashAttribute("examError", "Xóa thất bại: " + e.getMessage());
        }
        return "redirect:/teacher?activeTab=exams";
    }
}
