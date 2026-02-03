package com.EduQuiz.Project_intel.dto;

public class ExamCardDTO {
    private Long id;
    private String title;
    private String timeLabel;     
    private String dateLabel;     
    private String statusLabel;  
    private int questionCount;   
    private String thumbUrl;      

    public ExamCardDTO() {}

    public ExamCardDTO(Long id, String title, String timeLabel, String dateLabel,
                       String statusLabel, int questionCount, String thumbUrl) {
        this.id = id;
        this.title = title;
        this.timeLabel = timeLabel;
        this.dateLabel = dateLabel;
        this.statusLabel = statusLabel;
        this.questionCount = questionCount;
        this.thumbUrl = thumbUrl;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTimeLabel() { return timeLabel; }
    public void setTimeLabel(String timeLabel) { this.timeLabel = timeLabel; }

    public String getDateLabel() { return dateLabel; }
    public void setDateLabel(String dateLabel) { this.dateLabel = dateLabel; }

    public String getStatusLabel() { return statusLabel; }
    public void setStatusLabel(String statusLabel) { this.statusLabel = statusLabel; }

    public int getQuestionCount() { return questionCount; }
    public void setQuestionCount(int questionCount) { this.questionCount = questionCount; }

    public String getThumbUrl() { return thumbUrl; }
    public void setThumbUrl(String thumbUrl) { this.thumbUrl = thumbUrl; }
}
