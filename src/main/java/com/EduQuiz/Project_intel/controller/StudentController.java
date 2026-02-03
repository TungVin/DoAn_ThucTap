package com.EduQuiz.Project_intel.controller;

import com.EduQuiz.Project_intel.model.ExamAttempt;
import com.EduQuiz.Project_intel.model.Role;
import com.EduQuiz.Project_intel.model.User;
import com.EduQuiz.Project_intel.repository.ExamAttemptRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final ExamAttemptRepository attemptRepository;

    public StudentController(ExamAttemptRepository attemptRepository) {
        this.attemptRepository = attemptRepository;
    }

    @GetMapping
    public String studentPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/auth";

        // tránh lỗi nếu role null
        if (user.getRole() == null) {
            session.invalidate();
            return "redirect:/auth";
        }

        if (user.getRole() != Role.STUDENT) return "redirect:/teacher";

        // Lấy lịch sử bài làm của học sinh (mới nhất lên đầu)
        List<ExamAttempt> attempts = attemptRepository.findByStudentIdOrderBySubmittedAtDesc(user.getId());
        model.addAttribute("attempts", attempts);

        return "student";
    }

    /**
     * Trang chi tiết một lần làm bài (chỉ học sinh sở hữu attempt mới xem được)
     * URL: /student/attempts/{attemptId}
     */
    @GetMapping("/attempts/{attemptId}")
    public String attemptDetail(@PathVariable Long attemptId,
                                HttpSession session,
                                Model model) {

        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/auth";

        if (user.getRole() == null) {
            session.invalidate();
            return "redirect:/auth";
        }

        if (user.getRole() != Role.STUDENT) return "redirect:/teacher";

        ExamAttempt attempt = attemptRepository
                .findByIdAndStudentId(attemptId, user.getId())
                .orElse(null);

        if (attempt == null) return "redirect:/student";

        double pct = attempt.getPercent() == null ? 0.0 : attempt.getPercent();
        boolean passed = pct >= 50; // ✅ đổi ngưỡng pass ở đây nếu bạn muốn

        String submittedAtText = "";
        if (attempt.getSubmittedAt() != null) {
            submittedAtText = attempt.getSubmittedAt()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }

        model.addAttribute("attempt", attempt);
        model.addAttribute("pct", pct);
        model.addAttribute("passed", passed);
        model.addAttribute("submittedAtText", submittedAtText);

        return "student_attempt_detail";
    }
}
