package com.EduQuiz.Project_intel.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "exam_attempts")
public class ExamAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Học sinh làm bài
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    // Bài kiểm tra
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    // Thời điểm nộp bài
    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @Column(name = "score")
    private Double score;

    @Column(name = "total")
    private Double total;

    @Column(name = "percent")
    private Double percent;

    @Column(name = "answered_count")
    private Integer answeredCount;

    @Column(name = "correct_count")
    private Integer correctCount;

    @Column(name = "question_count")
    private Integer questionCount;

    public ExamAttempt() {
    }

    public ExamAttempt(User student, Exam exam, LocalDateTime submittedAt,
                       Double score, Double total, Double percent,
                       Integer answeredCount, Integer correctCount, Integer questionCount) {
        this.student = student;
        this.exam = exam;
        this.submittedAt = submittedAt;
        this.score = score;
        this.total = total;
        this.percent = percent;
        this.answeredCount = answeredCount;
        this.correctCount = correctCount;
        this.questionCount = questionCount;
    }

    /**
     * Nếu lúc lưu attempt chưa set submittedAt thì tự động gán thời gian hiện tại.
     */
    @PrePersist
    protected void onCreate() {
        if (this.submittedAt == null) {
            this.submittedAt = LocalDateTime.now();
        }
    }

    /**
     * Hiển thị thời gian nộp đẹp cho giao diện.
     */
    @Transient
    public String getSubmittedAtText() {
        if (submittedAt == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return submittedAt.format(formatter);
    }

    public Long getId() {
        return id;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getPercent() {
        return percent;
    }

    public void setPercent(Double percent) {
        this.percent = percent;
    }

    public Integer getAnsweredCount() {
        return answeredCount;
    }

    public void setAnsweredCount(Integer answeredCount) {
        this.answeredCount = answeredCount;
    }

    public Integer getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(Integer correctCount) {
        this.correctCount = correctCount;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }
}