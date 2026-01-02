package com.EduQuiz.Project_intel.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exams")
public class Exam {

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

    private Integer timeLimit; // in minutes

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Column(nullable = false)
    private String status = "draft"; // draft, ongoing, completed

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters and Setters
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

