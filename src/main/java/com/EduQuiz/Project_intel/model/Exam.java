package com.EduQuiz.Project_intel.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exams")
public class Exam {

    // ===== Enum cấu hình hiển thị kết quả =====
    public enum ResultDisplayMode {
        AFTER_TEACHER_REVIEW,   // Sau khi giáo viên chấm bài
        AFTER_EXAM_TIME_END,    // Sau khi kết thúc thời gian làm bài
        AFTER_SUBMIT            // Sau khi học sinh nộp bài
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    // Thời gian làm bài (phút)
    private Integer timeLimit; // in minutes

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Column(nullable = false)
    private String status = "draft"; // draft, ongoing, ended

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // ===== CÀI ĐẶT NÂNG CAO =====

    // Khi nào hiển thị kết quả
    @Enumerated(EnumType.STRING)
    @Column(name = "result_display_mode")
    private ResultDisplayMode resultDisplayMode = ResultDisplayMode.AFTER_TEACHER_REVIEW;

    // Hệ thống tự động chia điểm
    @Column(name = "auto_divide_score")
    private Boolean autoDivideScore = Boolean.TRUE;

    // Điểm tối đa cho bài kiểm tra
    @Column(name = "max_score")
    private Integer maxScore = 10;

    // Số lần nộp bài
    @Column(name = "max_attempts")
    private Integer maxAttempts = 1;

    // Kiểu đánh số câu hỏi: NUMBER (1,2,3), LETTER (A,B,C), NONE
    @Column(name = "question_number_style")
    private String questionNumberStyle = "LETTER";

    // Số câu hỏi trên 1 trang (0 = tất cả)
    @Column(name = "questions_per_page")
    private Integer questionsPerPage = 50;

    // Số đáp án trên 1 hàng
    @Column(name = "answers_per_row")
    private Integer answersPerRow = 1;

    // ===== Getters và Setters =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ResultDisplayMode getResultDisplayMode() {
        return resultDisplayMode;
    }

    public void setResultDisplayMode(ResultDisplayMode resultDisplayMode) {
        this.resultDisplayMode = resultDisplayMode;
    }

    public Boolean getAutoDivideScore() {
        return autoDivideScore;
    }

    public void setAutoDivideScore(Boolean autoDivideScore) {
        this.autoDivideScore = autoDivideScore;
    }

    public Integer getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Integer maxScore) {
        this.maxScore = maxScore;
    }

    public Integer getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public String getQuestionNumberStyle() {
        return questionNumberStyle;
    }

    public void setQuestionNumberStyle(String questionNumberStyle) {
        this.questionNumberStyle = questionNumberStyle;
    }

    public Integer getQuestionsPerPage() {
        return questionsPerPage;
    }

    public void setQuestionsPerPage(Integer questionsPerPage) {
        this.questionsPerPage = questionsPerPage;
    }

    public Integer getAnswersPerRow() {
        return answersPerRow;
    }

    public void setAnswersPerRow(Integer answersPerRow) {
        this.answersPerRow = answersPerRow;
    }

    /**
     * Tự động cập nhật trạng thái dựa trên thời gian hiện tại
     */
    public void updateStatus() {
        if (startTime == null || endTime == null) {
            // Nếu không có thời gian, giữ nguyên status hiện tại
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(startTime)) {
            // Chưa đến giờ bắt đầu
            this.status = "draft";
        } else if (now.isAfter(endTime)) {
            // Đã kết thúc
            this.status = "ended";
        } else {
            // Đang diễn ra
            this.status = "ongoing";
        }
    }

    /**
     * Get status với tự động cập nhật
     */
    public String getCurrentStatus() {
        updateStatus();
        return this.status;
    }
}
