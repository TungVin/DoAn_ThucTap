package com.EduQuiz.Project_intel.dto;

import com.EduQuiz.Project_intel.model.Exam.ResultDisplayMode;

public class ExamUpsertForm {

    // dùng cho update (tạo mới thì để null)
    private Long id;

    // --- Thông tin chung ---
    private String title;
    private String description;
    private Long categoryId;

    // --- Cài đặt thời gian ---
    private boolean timeLimitEnabled;
    private Integer timeLimit; // phút

    private boolean startEnabled;
    private String startDate;  // yyyy-MM-dd
    private String startTime;  // HH:mm

    private boolean endEnabled;
    private String endDate;
    private String endTime;

    // --- CÀI ĐẶT NÂNG CAO (giống NineQuiz) ---

    // Khi nào hiển thị kết quả
    private ResultDisplayMode resultDisplayMode = ResultDisplayMode.AFTER_TEACHER_REVIEW;

    // Hệ thống tự động chia điểm
    private boolean autoDivideScore = true;

    // Điểm tối đa cho bài kiểm tra
    private Integer maxScore = 10;

    // Số lần nộp bài
    private Integer maxAttempts = 1;

    // Kiểu số thứ tự câu hỏi: NUMBER / LETTER / NONE
    private String questionNumberStyle = "LETTER";

    // Số câu hỏi trên 1 trang (0 = tất cả)
    private Integer questionsPerPage = 50;

    // Số câu trả lời trên 1 hàng
    private Integer answersPerRow = 1;

    // ===== GETTER / SETTER =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public boolean isTimeLimitEnabled() { return timeLimitEnabled; }
    public void setTimeLimitEnabled(boolean timeLimitEnabled) { this.timeLimitEnabled = timeLimitEnabled; }

    public Integer getTimeLimit() { return timeLimit; }
    public void setTimeLimit(Integer timeLimit) { this.timeLimit = timeLimit; }

    public boolean isStartEnabled() { return startEnabled; }
    public void setStartEnabled(boolean startEnabled) { this.startEnabled = startEnabled; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public boolean isEndEnabled() { return endEnabled; }
    public void setEndEnabled(boolean endEnabled) { this.endEnabled = endEnabled; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public ResultDisplayMode getResultDisplayMode() { return resultDisplayMode; }
    public void setResultDisplayMode(ResultDisplayMode resultDisplayMode) { this.resultDisplayMode = resultDisplayMode; }

    public boolean isAutoDivideScore() { return autoDivideScore; }
    public void setAutoDivideScore(boolean autoDivideScore) { this.autoDivideScore = autoDivideScore; }

    public Integer getMaxScore() { return maxScore; }
    public void setMaxScore(Integer maxScore) { this.maxScore = maxScore; }

    public Integer getMaxAttempts() { return maxAttempts; }
    public void setMaxAttempts(Integer maxAttempts) { this.maxAttempts = maxAttempts; }

    public String getQuestionNumberStyle() { return questionNumberStyle; }
    public void setQuestionNumberStyle(String questionNumberStyle) { this.questionNumberStyle = questionNumberStyle; }

    public Integer getQuestionsPerPage() { return questionsPerPage; }
    public void setQuestionsPerPage(Integer questionsPerPage) { this.questionsPerPage = questionsPerPage; }

    public Integer getAnswersPerRow() { return answersPerRow; }
    public void setAnswersPerRow(Integer answersPerRow) { this.answersPerRow = answersPerRow; }
}
