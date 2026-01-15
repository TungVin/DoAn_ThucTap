package com.EduQuiz.Project_intel.dto;

public class ExamCardDTO {
    private Long id;
    private String title;
    private String timeLabel;     // "15 phút" / "Không giới hạn"
    private String dateLabel;     // "Thg 01 15"
    private String statusLabel;   // tạm: "Bản nháp"
    private int questionCount;    // tạm: 0
    private String thumbUrl;      // tạm: null (template dùng ảnh default)

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
