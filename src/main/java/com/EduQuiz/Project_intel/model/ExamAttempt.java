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

    // học sinh làm bài
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    // bài kiểm tra
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    // thời điểm nộp bài
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    private Double score;
    private Double total;
    private Double percent;

    @Column(name = "answered_count")
    private Integer answeredCount;

    @Column(name = "correct_count")
    private Integer correctCount;

    @Column(name = "question_count")
    private Integer questionCount;

    public ExamAttempt() {}

    /**
     * Nếu lúc lưu attempt bạn quên set submittedAt thì tự set.
     * Không ảnh hưởng nếu bạn đã set rồi.
     */
    @PrePersist
    protected void onCreate() {
        if (this.submittedAt == null) {
            this.submittedAt = LocalDateTime.now();
        }
    }

    // ====== Getter hiển thị thời gian đẹp cho UI ======
    @Transient
    public String getSubmittedAtText() {
        if (submittedAt == null) return "";
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return submittedAt.format(fmt);
    }

    // ===== getters/setters =====
    public Long getId() { return id; }

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    public Exam getExam() { return exam; }
    public void setExam(Exam exam) { this.exam = exam; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public Double getPercent() { return percent; }
    public void setPercent(Double percent) { this.percent = percent; }

    public Integer getAnsweredCount() { return answeredCount; }
    public void setAnsweredCount(Integer answeredCount) { this.answeredCount = answeredCount; }

    public Integer getCorrectCount() { return correctCount; }
    public void setCorrectCount(Integer correctCount) { this.correctCount = correctCount; }

    public Integer getQuestionCount() { return questionCount; }
    public void setQuestionCount(Integer questionCount) { this.questionCount = questionCount; }
}
