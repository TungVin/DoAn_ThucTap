package com.EduQuiz.Project_intel.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exams")
public class Exam {

   
    public enum ResultDisplayMode {
        AFTER_TEACHER_REVIEW,   
        AFTER_EXAM_TIME_END,   
        AFTER_SUBMIT            
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

    
    private Integer timeLimit; 

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Column(nullable = false)
    private String status = "draft"; 

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    

    
    @Enumerated(EnumType.STRING)
    @Column(name = "result_display_mode")
    private ResultDisplayMode resultDisplayMode = ResultDisplayMode.AFTER_TEACHER_REVIEW;

    
    @Column(name = "auto_divide_score")
    private Boolean autoDivideScore = Boolean.TRUE;

    
    @Column(name = "max_score")
    private Integer maxScore = 10;

    
    @Column(name = "max_attempts")
    private Integer maxAttempts = 1;

    
    @Column(name = "question_number_style")
    private String questionNumberStyle = "LETTER";

   
    @Column(name = "questions_per_page")
    private Integer questionsPerPage = 50;

    
    @Column(name = "answers_per_row")
    private Integer answersPerRow = 1;

    
    @Column(name = "is_public")
    private Boolean isPublic = Boolean.FALSE;  

   

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

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }


    public void updateStatus() {
        if (startTime == null || endTime == null) {
            
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(startTime)) {
           
            this.status = "draft";
        } else if (now.isAfter(endTime)) {
           
            this.status = "ended";
        } else {
            
            this.status = "ongoing";
        }
    }

   
    public String getCurrentStatus() {
        updateStatus();
        return this.status;
    }
}
