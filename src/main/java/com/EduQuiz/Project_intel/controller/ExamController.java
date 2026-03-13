package com.EduQuiz.Project_intel.controller;

import com.EduQuiz.Project_intel.dto.ExamUpsertForm;
import com.EduQuiz.Project_intel.model.Exam;
import com.EduQuiz.Project_intel.model.ExamAttempt;
import com.EduQuiz.Project_intel.model.Role;
import com.EduQuiz.Project_intel.model.User;
import com.EduQuiz.Project_intel.repository.ExamAttemptRepository;
import com.EduQuiz.Project_intel.service.CategoryService;
import com.EduQuiz.Project_intel.service.ExamQuestionItemService;
import com.EduQuiz.Project_intel.service.ExamService;
import jakarta.servlet.http.HttpSession;
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
    private final ExamAttemptRepository examAttemptRepository;

    public ExamController(ExamService examService,
                          CategoryService categoryService,
                          ExamQuestionItemService examQuestionItemService,
                          ExamAttemptRepository examAttemptRepository) {
        this.examService = examService;
        this.categoryService = categoryService;
        this.examQuestionItemService = examQuestionItemService;
        this.examAttemptRepository = examAttemptRepository;
    }

    private String requireTeacher(HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/auth";
        }

        if (user.getRole() == null) {
            session.invalidate();
            return "redirect:/auth";
        }

        if (user.getRole() != Role.TEACHER) {
            return "redirect:/student";
        }

        return null;
    }

    @GetMapping
    public String list(Model model, HttpSession session) {
        String guard = requireTeacher(session);
        if (guard != null) return guard;

        model.addAttribute("exams", examService.getAll());
        return "teacher/exams";
    }

    @GetMapping("/new")
    public String newExam(Model model, HttpSession session) {
        String guard = requireTeacher(session);
        if (guard != null) return guard;

        model.addAttribute("mode", "create");
        model.addAttribute("form", new ExamUpsertForm());
        model.addAttribute("categories", categoryService.getAll());
        return "teacher/exam-editor";
    }

    @PostMapping
    public String createExam(@ModelAttribute("form") ExamUpsertForm form,
                             @RequestParam(value = "questionsJson", required = false) String questionsJson,
                             RedirectAttributes ra,
                             HttpSession session) {
        String guard = requireTeacher(session);
        if (guard != null) return guard;

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
    public String editExam(@PathVariable("id") Long id,
                           Model model,
                           HttpSession session) {
        String guard = requireTeacher(session);
        if (guard != null) return guard;

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
                             RedirectAttributes ra,
                             HttpSession session) {
        String guard = requireTeacher(session);
        if (guard != null) return guard;

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
    public String deleteExam(@RequestParam("id") Long id,
                             RedirectAttributes ra,
                             HttpSession session) {
        String guard = requireTeacher(session);
        if (guard != null) return guard;

        try {
            examService.deleteById(id);
            ra.addFlashAttribute("toast", "Đã xóa bài kiểm tra!");
        } catch (Exception e) {
            ra.addFlashAttribute("examError", "Xóa thất bại: " + e.getMessage());
        }
        return "redirect:/teacher?activeTab=exams";
    }

    @GetMapping("/{id}/stats")
    public String examStats(@PathVariable("id") Long id,
                            Model model,
                            RedirectAttributes ra,
                            HttpSession session) {
        String guard = requireTeacher(session);
        if (guard != null) return guard;

        try {
            Exam exam = examService.findById(id);

            model.addAttribute("exam", exam);
            model.addAttribute("attempts", examService.getAttemptsByExam(id));
            model.addAttribute("attemptCount", examService.countAttemptsByExam(id));

            return "teacher/exam-stats";
        } catch (Exception e) {
            ra.addFlashAttribute("examError", "Không tìm thấy thống kê bài kiểm tra!");
            return "redirect:/teacher?activeTab=exams";
        }
    }

    @GetMapping("/attempts/{attemptId}")
    public String attemptDetailForTeacher(@PathVariable Long attemptId,
                                          Model model,
                                          RedirectAttributes ra,
                                          HttpSession session) {
        String guard = requireTeacher(session);
        if (guard != null) return guard;

        ExamAttempt attempt = examAttemptRepository.findById(attemptId).orElse(null);

        if (attempt == null) {
            ra.addFlashAttribute("examError", "Không tìm thấy bài làm.");
            return "redirect:/teacher?activeTab=exams";
        }

        model.addAttribute("attempt", attempt);
        model.addAttribute("exam", attempt.getExam());
        model.addAttribute("student", attempt.getStudent());

        Double pct = attempt.getPercent() != null ? attempt.getPercent() : 0.0;
        model.addAttribute("pct", pct);
        model.addAttribute("passed", pct >= 50);

        return "teacher/attempt-detail";
    }
}