package com.EduQuiz.Project_intel.dto;

import com.EduQuiz.Project_intel.model.Exam.ResultDisplayMode;

public class ExamUpsertForm {

    
    private Long id;

    
    private String title;
    private String description;
    private Long categoryId;

   
    private boolean timeLimitEnabled;
    private Integer timeLimit; 

    private boolean startEnabled;
    private String startDate;  
    private String startTime;  

    private boolean endEnabled;
    private String endDate;
    private String endTime;

    
    private ResultDisplayMode resultDisplayMode = ResultDisplayMode.AFTER_TEACHER_REVIEW;

   
    private boolean autoDivideScore = true;


    private Integer maxScore = 10;

    
    private Integer maxAttempts = 1;

   
    private String questionNumberStyle = "LETTER";

    
    private Integer questionsPerPage = 50;

    
    private Integer answersPerRow = 1;

    
    private Boolean isPublic = false; 

 

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

    // --- Getter/Setter mới cho isPublic ---
    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }
}
