package com.EduQuiz.Project_intel.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exam_question_items")
public class ExamQuestionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @Column(nullable = false, length = 4000)
    private String content;

    @Column(nullable = false)
    private String type = "single_choice"; 

    @Column(nullable = false)
    private Integer orderIndex = 0;

    @Column(nullable = false)
    private Double score = 1.0;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<ExamAnswerOption> options = new ArrayList<>();

    
    public Long getId() { return id; }
    public Exam getExam() { return exam; }
    public void setExam(Exam exam) { this.exam = exam; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public List<ExamAnswerOption> getOptions() { return options; }
    public void setOptions(List<ExamAnswerOption> options) { this.options = options; }
}
