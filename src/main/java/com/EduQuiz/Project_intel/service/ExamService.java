package com.EduQuiz.Project_intel.service;

import com.EduQuiz.Project_intel.dto.ExamUpsertForm;
import com.EduQuiz.Project_intel.model.Category;
import com.EduQuiz.Project_intel.model.Exam;
import com.EduQuiz.Project_intel.repository.CategoryRepository;
import com.EduQuiz.Project_intel.repository.ExamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExamService {

    private final ExamRepository examRepository;
    private final CategoryRepository categoryRepository;

    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public ExamService(ExamRepository examRepository, CategoryRepository categoryRepository) {
        this.examRepository = examRepository;
        this.categoryRepository = categoryRepository;
    }

    // Lấy tất cả exam
    @Transactional(readOnly = true)
    public List<Exam> getAll() {
        return examRepository.findAll();
    }

    // Tạo mới từ form
    @Transactional
    public Long createFromForm(ExamUpsertForm form) {
        Exam exam = new Exam();
        applyForm(exam, form);
        examRepository.save(exam);
        return exam.getId();
    }

    // Lấy form để edit
    @Transactional(readOnly = true)
    public ExamUpsertForm getFormById(Long id) {
        Exam exam = examRepository.findById(id).orElseThrow();

        ExamUpsertForm f = new ExamUpsertForm();
        f.setId(exam.getId());
        f.setTitle(exam.getTitle());
        f.setDescription(exam.getDescription());

        if (exam.getCategory() != null) {
            f.setCategoryId(exam.getCategory().getId());
        }

        // ========== THỜI GIAN ==========
        if (exam.getTimeLimit() != null) {
            f.setTimeLimitEnabled(true);
            f.setTimeLimit(exam.getTimeLimit());
        }

        if (exam.getStartTime() != null) {
            f.setStartEnabled(true);
            f.setStartDate(exam.getStartTime().toLocalDate().toString());
            f.setStartTime(exam.getStartTime().toLocalTime().toString().substring(0, 5));
        }

        if (exam.getEndTime() != null) {
            f.setEndEnabled(true);
            f.setEndDate(exam.getEndTime().toLocalDate().toString());
            f.setEndTime(exam.getEndTime().toLocalTime().toString().substring(0, 5));
        }

        // ========== CÀI ĐẶT NÂNG CAO ==========
        if (exam.getResultDisplayMode() != null) {
            f.setResultDisplayMode(exam.getResultDisplayMode());
        }

        if (exam.getAutoDivideScore() != null) {
            f.setAutoDivideScore(exam.getAutoDivideScore());
        }

        if (exam.getMaxScore() != null) {
            f.setMaxScore(exam.getMaxScore());
        }

        if (exam.getMaxAttempts() != null) {
            f.setMaxAttempts(exam.getMaxAttempts());
        }

        if (exam.getQuestionNumberStyle() != null) {
            f.setQuestionNumberStyle(exam.getQuestionNumberStyle());
        }

        if (exam.getQuestionsPerPage() != null) {
            f.setQuestionsPerPage(exam.getQuestionsPerPage());
        }

        if (exam.getAnswersPerRow() != null) {
            f.setAnswersPerRow(exam.getAnswersPerRow());
        }

        return f;
    }

    // Cập nhật từ form
    @Transactional
    public void updateFromForm(Long id, ExamUpsertForm form) {
        Exam exam = examRepository.findById(id).orElseThrow();
        applyForm(exam, form);
        examRepository.save(exam);
    }

    // Ánh xạ dữ liệu từ form -> entity
    private void applyForm(Exam exam, ExamUpsertForm f) {
        exam.setTitle(f.getTitle());
        exam.setDescription(f.getDescription());

        // Category
        if (f.getCategoryId() != null) {
            Category cat = categoryRepository.findById(f.getCategoryId()).orElseThrow();
            exam.setCategory(cat);
        } else {
            exam.setCategory(null);
        }

        // ========== THỜI GIAN ==========
        if (!f.isTimeLimitEnabled()) {
            exam.setTimeLimit(null);
        } else {
            exam.setTimeLimit(f.getTimeLimit());
        }

        exam.setStartTime(parseLocalDateTime(f.isStartEnabled(), f.getStartDate(), f.getStartTime()));
        exam.setEndTime(parseLocalDateTime(f.isEndEnabled(), f.getEndDate(), f.getEndTime()));

        if (exam.getStartTime() != null && exam.getEndTime() != null
                && exam.getEndTime().isBefore(exam.getStartTime())) {
            throw new IllegalArgumentException("Thời gian kết thúc phải sau thời gian bắt đầu");
        }

        // ========== CÀI ĐẶT NÂNG CAO ==========
        // Khi nào hiển thị kết quả
        if (f.getResultDisplayMode() != null) {
            exam.setResultDisplayMode(f.getResultDisplayMode());
        } else if (exam.getResultDisplayMode() == null) {
            exam.setResultDisplayMode(Exam.ResultDisplayMode.AFTER_TEACHER_REVIEW);
        }

        // Tự động chia điểm
        exam.setAutoDivideScore(f.isAutoDivideScore());

        // Điểm tối đa
        if (f.getMaxScore() != null) {
            exam.setMaxScore(f.getMaxScore());
        } else if (exam.getMaxScore() == null) {
            exam.setMaxScore(10);
        }

        // Số lần nộp bài
        if (f.getMaxAttempts() != null) {
            exam.setMaxAttempts(f.getMaxAttempts());
        } else if (exam.getMaxAttempts() == null) {
            exam.setMaxAttempts(1);
        }

        // Kiểu số thứ tự câu hỏi
        if (f.getQuestionNumberStyle() != null && !f.getQuestionNumberStyle().isBlank()) {
            exam.setQuestionNumberStyle(f.getQuestionNumberStyle());
        } else if (exam.getQuestionNumberStyle() == null) {
            exam.setQuestionNumberStyle("LETTER");
        }

        // Số câu trên 1 trang
        if (f.getQuestionsPerPage() != null) {
            exam.setQuestionsPerPage(f.getQuestionsPerPage());
        } else if (exam.getQuestionsPerPage() == null) {
            exam.setQuestionsPerPage(50);
        }

        // Số đáp án trên 1 hàng
        if (f.getAnswersPerRow() != null) {
            exam.setAnswersPerRow(f.getAnswersPerRow());
        } else if (exam.getAnswersPerRow() == null) {
            exam.setAnswersPerRow(1);
        }
    }

    private LocalDateTime parseLocalDateTime(boolean enabled, String date, String time) {
        if (!enabled) return null;
        if (date == null || date.isBlank() || time == null || time.isBlank()) return null;
        return LocalDateTime.parse(date + "T" + time, DATE_TIME_FMT);
    }
}
