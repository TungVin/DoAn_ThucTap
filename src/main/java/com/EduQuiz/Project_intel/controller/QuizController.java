package com.EduQuiz.Project_intel.controller;

import com.EduQuiz.Project_intel.model.Exam;
import com.EduQuiz.Project_intel.model.ExamAttempt;
import com.EduQuiz.Project_intel.model.ExamQuestionItem;
import com.EduQuiz.Project_intel.model.Role;
import com.EduQuiz.Project_intel.model.User;
import com.EduQuiz.Project_intel.repository.ExamAttemptRepository;
import com.EduQuiz.Project_intel.repository.ExamRepository;
import com.EduQuiz.Project_intel.service.ExamQuestionItemService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/quiz")
public class QuizController {

    private final ExamRepository examRepository;
    private final ExamQuestionItemService questionItemService;
    private final ExamAttemptRepository attemptRepository;

    public QuizController(ExamRepository examRepository,
                          ExamQuestionItemService questionItemService,
                          ExamAttemptRepository attemptRepository) {
        this.examRepository = examRepository;
        this.questionItemService = questionItemService;
        this.attemptRepository = attemptRepository;
    }

    /**
     * Bắt buộc đăng nhập và phải là STUDENT.
     * - Chưa login => /auth?redirect=<current_url>
     * - Login TEACHER => /teacher
     */
    private String requireStudent(HttpSession session, HttpServletRequest request) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return redirectToAuthWithCurrentUrl(request);
        }

        // chống null role (tránh redirect loop nếu DB có user role null)
        if (user.getRole() == null) {
            session.invalidate();
            return "redirect:/auth";
        }

        if (user.getRole() != Role.STUDENT) {
            return "redirect:/teacher";
        }

        return null; // ok
    }

    private String redirectToAuthWithCurrentUrl(HttpServletRequest request) {
        String full = request.getRequestURI();
        String qs = request.getQueryString();
        if (qs != null && !qs.isBlank()) {
            full += "?" + qs;
        }
        String encoded = URLEncoder.encode(full, StandardCharsets.UTF_8);
        return "redirect:/auth?redirect=" + encoded;
    }

    @GetMapping("/{examId}")
    public String intro(@PathVariable Long examId,
                        Model model,
                        HttpSession session,
                        HttpServletRequest request) {

        String guard = requireStudent(session, request);
        if (guard != null) return guard;

        Exam exam = examRepository.findById(examId).orElse(null);
        if (exam == null) return "redirect:/";

        List<ExamQuestionItem> questions = questionItemService.getByExam(examId);

        double totalScore = questions.stream()
                .mapToDouble(q -> q.getScore() == null ? 0.0 : q.getScore())
                .sum();

        BlockInfo block = checkTimeWindow(exam);
        model.addAttribute("blocked", block.blocked);
        model.addAttribute("blockMessage", block.message);

        model.addAttribute("exam", exam);
        model.addAttribute("questionCount", questions.size());
        model.addAttribute("totalScore", totalScore);

        boolean showTimer = isTimeLimitEnabled(exam);
        model.addAttribute("showTimer", showTimer);

        return "quiz/intro";
    }

    @GetMapping("/{examId}/take")
    public String take(@PathVariable Long examId,
                       Model model,
                       HttpSession session,
                       HttpServletRequest request) {

        String guard = requireStudent(session, request);
        if (guard != null) return guard;

        Exam exam = examRepository.findById(examId).orElse(null);
        if (exam == null) return "redirect:/";

        BlockInfo block = checkTimeWindow(exam);
        if (block.blocked) {
            model.addAttribute("exam", exam);
            model.addAttribute("blockMessage", block.message);
            return "quiz/blocked";
        }

        List<ExamQuestionItem> questions = questionItemService.getByExam(examId);

        double totalScore = questions.stream()
                .mapToDouble(q -> q.getScore() == null ? 0.0 : q.getScore())
                .sum();

        model.addAttribute("exam", exam);
        model.addAttribute("questions", questions);
        model.addAttribute("questionCount", questions.size());
        model.addAttribute("totalScore", totalScore);

        boolean showTimer = isTimeLimitEnabled(exam);
        model.addAttribute("showTimer", showTimer);

        Integer timeLimit = getTimeLimitMinutes(exam);
        model.addAttribute("timeLimitMinutes", timeLimit == null ? 0 : timeLimit);

        return "quiz/take";
    }

    @PostMapping("/{examId}/submit")
    public String submit(@PathVariable Long examId,
                         @RequestParam Map<String, String> params,
                         Model model,
                         HttpSession session,
                         HttpServletRequest request) {

        String guard = requireStudent(session, request);
        if (guard != null) return guard;

        Exam exam = examRepository.findById(examId).orElse(null);
        if (exam == null) return "redirect:/";

        List<ExamQuestionItem> questions = questionItemService.getByExam(examId);

        double total = questions.stream()
                .mapToDouble(q -> q.getScore() == null ? 0.0 : q.getScore())
                .sum();

        double score = 0.0;
        int answered = 0;
        int correctCount = 0;

        for (ExamQuestionItem q : questions) {
            String key = "q_" + q.getId();
            String selected = params.get(key);
            if (selected == null || selected.isBlank()) continue;

            answered++;

            Long optId;
            try {
                optId = Long.parseLong(selected.trim());
            } catch (Exception e) {
                continue; // param lỗi thì bỏ qua, không crash
            }

            boolean correct = q.getOptions().stream()
                    .anyMatch(o -> o.getId().equals(optId) && Boolean.TRUE.equals(o.getCorrect()));

            if (correct) {
                correctCount++;
                score += (q.getScore() == null ? 0.0 : q.getScore());
            }
        }

        double percent = total > 0 ? (score / total) * 100.0 : 0.0;
        double percentRounded = Math.round(percent * 10.0) / 10.0;

        // ===== LƯU LỊCH SỬ BÀI LÀM =====
        User student = (User) session.getAttribute("user"); // chắc chắn != null do requireStudent
        ExamAttempt attempt = new ExamAttempt();
        attempt.setStudent(student);
        attempt.setExam(exam);
        attempt.setSubmittedAt(LocalDateTime.now());
        attempt.setScore(score);
        attempt.setTotal(total);
        attempt.setPercent(percentRounded);
        attempt.setAnsweredCount(answered);
        attempt.setCorrectCount(correctCount);
        attempt.setQuestionCount(questions.size());
        attemptRepository.save(attempt);

        // ===== TRẢ VIEW RESULT =====
        model.addAttribute("exam", exam);
        model.addAttribute("score", score);
        model.addAttribute("total", total);

        model.addAttribute("answeredCount", answered);
        model.addAttribute("correctCount", correctCount);
        model.addAttribute("questionCount", questions.size());

        model.addAttribute("percent", percentRounded);

        return "quiz/result";
    }

    // ====== Các hàm hiện có của bạn giữ nguyên ======

    private boolean isTimeLimitEnabled(Exam exam) {
        try {
            var m = exam.getClass().getMethod("isTimeLimitEnabled");
            Object val = m.invoke(exam);
            if (val instanceof Boolean) return (Boolean) val;
        } catch (Exception ignored) { }
        Integer tl = getTimeLimitMinutes(exam);
        return tl != null && tl > 0;
    }

    private Integer getTimeLimitMinutes(Exam exam) {
        try {
            var m = exam.getClass().getMethod("getTimeLimit");
            Object val = m.invoke(exam);
            if (val instanceof Integer) return (Integer) val;
        } catch (Exception ignored) { }
        return null;
    }

    private BlockInfo checkTimeWindow(Exam exam) {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime start = parseExamDateTime(exam, true);
        LocalDateTime end = parseExamDateTime(exam, false);

        Boolean startEnabled = getBoolean(exam, "isStartEnabled", "getStartEnabled");
        Boolean endEnabled = getBoolean(exam, "isEndEnabled", "getEndEnabled");

        if (Boolean.TRUE.equals(startEnabled) && start != null && now.isBefore(start)) {
            return new BlockInfo(true, "Bài kiểm tra chưa đến thời gian bắt đầu.");
        }
        if (Boolean.TRUE.equals(endEnabled) && end != null && now.isAfter(end)) {
            return new BlockInfo(true, "Bài kiểm tra đã hết hạn.");
        }
        return new BlockInfo(false, "");
    }

    private LocalDateTime parseExamDateTime(Exam exam, boolean isStart) {
        String date = getString(exam, isStart ? "getStartDate" : "getEndDate");
        String time = getString(exam, isStart ? "getStartTime" : "getEndTime");

        if (date == null || date.isBlank()) return null;

        String t = (time == null || time.isBlank())
                ? (isStart ? "00:00" : "23:59")
                : time;

        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            return LocalDateTime.parse(date.trim() + " " + t.trim(), fmt);
        } catch (Exception e) {
            return null;
        }
    }

    private String getString(Exam exam, String getter) {
        try {
            var m = exam.getClass().getMethod(getter);
            Object val = m.invoke(exam);
            return val == null ? null : String.valueOf(val);
        } catch (Exception ignored) {
            return null;
        }
    }

    private Boolean getBoolean(Exam exam, String boolGetter1, String boolGetter2) {
        try {
            var m = exam.getClass().getMethod(boolGetter1);
            Object val = m.invoke(exam);
            if (val instanceof Boolean) return (Boolean) val;
        } catch (Exception ignored) { }

        try {
            var m = exam.getClass().getMethod(boolGetter2);
            Object val = m.invoke(exam);
            if (val instanceof Boolean) return (Boolean) val;
        } catch (Exception ignored) { }

        return null;
    }

    private static class BlockInfo {
        boolean blocked;
        String message;

        BlockInfo(boolean blocked, String message) {
            this.blocked = blocked;
            this.message = message;
        }
    }
}
