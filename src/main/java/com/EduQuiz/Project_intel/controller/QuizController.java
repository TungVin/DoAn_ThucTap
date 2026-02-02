package com.EduQuiz.Project_intel.controller;

import com.EduQuiz.Project_intel.model.Exam;
import com.EduQuiz.Project_intel.model.ExamQuestionItem;
import com.EduQuiz.Project_intel.repository.ExamRepository;
import com.EduQuiz.Project_intel.service.ExamQuestionItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/quiz")
public class QuizController {

    private final ExamRepository examRepository;
    private final ExamQuestionItemService questionItemService;

    public QuizController(ExamRepository examRepository, ExamQuestionItemService questionItemService) {
        this.examRepository = examRepository;
        this.questionItemService = questionItemService;
    }

    @GetMapping("/{examId}")
    public String intro(@PathVariable Long examId, Model model) {
        Exam exam = examRepository.findById(examId).orElse(null);
        if (exam == null) return "redirect:/";

        List<ExamQuestionItem> questions = questionItemService.getByExam(examId);

        double totalScore = questions.stream()
                .mapToDouble(q -> q.getScore() == null ? 0.0 : q.getScore())
                .sum();

        // check thời gian
        BlockInfo block = checkTimeWindow(exam);
        model.addAttribute("blocked", block.blocked);
        model.addAttribute("blockMessage", block.message);

        model.addAttribute("exam", exam);
        model.addAttribute("questionCount", questions.size());
        model.addAttribute("totalScore", totalScore);

        // Timer: chỉ hiển thị nếu bật giới hạn thời gian
        boolean showTimer = isTimeLimitEnabled(exam);
        model.addAttribute("showTimer", showTimer);

        return "quiz/intro";
    }

    @GetMapping("/{examId}/take")
    public String take(@PathVariable Long examId, Model model) {
        Exam exam = examRepository.findById(examId).orElse(null);
        if (exam == null) return "redirect:/";

        // chặn theo thời gian
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

        // Timer: chỉ hiển thị nếu bật giới hạn thời gian
        boolean showTimer = isTimeLimitEnabled(exam);
        model.addAttribute("showTimer", showTimer);

        // phút: nếu null thì 0
        Integer timeLimit = getTimeLimitMinutes(exam);
        model.addAttribute("timeLimitMinutes", timeLimit == null ? 0 : timeLimit);

        return "quiz/take";
    }

    @PostMapping("/{examId}/submit")
    public String submit(@PathVariable Long examId,
                         @RequestParam Map<String, String> params,
                         Model model) {

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

        model.addAttribute("exam", exam);
        model.addAttribute("score", score);
        model.addAttribute("total", total);

        // thêm chi tiết
        model.addAttribute("answeredCount", answered);
        model.addAttribute("correctCount", correctCount);
        model.addAttribute("questionCount", questions.size());

        // phần trăm
        double percent = total > 0 ? (score / total) * 100.0 : 0.0;
        model.addAttribute("percent", Math.round(percent * 10.0) / 10.0);

        return "quiz/result";
    }

    // =========================
    // Helpers
    // =========================

    private boolean isTimeLimitEnabled(Exam exam) {
        // nếu bạn có field timeLimitEnabled trong Exam thì dùng nó.
        // nếu không có, chỉ cần timeLimit != null là coi như enabled.
        try {
            // reflection tránh compile lỗi nếu field không tồn tại
            var m = exam.getClass().getMethod("isTimeLimitEnabled");
            Object val = m.invoke(exam);
            if (val instanceof Boolean) return (Boolean) val;
        } catch (Exception ignored) { }
        return getTimeLimitMinutes(exam) != null && getTimeLimitMinutes(exam) > 0;
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
        // nếu bạn không lưu start/end trong Exam thì coi như không block
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
        // Lấy startDate/startTime hoặc endDate/endTime dạng String "yyyy-MM-dd" + "HH:mm"
        String date = getString(exam, isStart ? "getStartDate" : "getEndDate");
        String time = getString(exam, isStart ? "getStartTime" : "getEndTime");

        if (date == null || date.isBlank()) return null;

        // nếu time trống thì mặc định 00:00 hoặc 23:59
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
