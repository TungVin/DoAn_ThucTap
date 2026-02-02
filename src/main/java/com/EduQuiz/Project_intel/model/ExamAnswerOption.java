package com.EduQuiz.Project_intel.model;

import jakarta.persistence.*;

@Entity
@Table(name = "exam_answer_options")
public class ExamAnswerOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id")
    private ExamQuestionItem question;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(name = "attachment_url")
    private String attachmentUrl;

    @Column(nullable = false)
    private Boolean correct = Boolean.FALSE;

    @Column(nullable = false)
    private Integer orderIndex = 0;

    // getters/setters
    public Long getId() { return id; }

    public ExamQuestionItem getQuestion() { return question; }
    public void setQuestion(ExamQuestionItem question) { this.question = question; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }

    public Boolean getCorrect() { return correct; }
    public void setCorrect(Boolean correct) { this.correct = correct; }

    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
}
