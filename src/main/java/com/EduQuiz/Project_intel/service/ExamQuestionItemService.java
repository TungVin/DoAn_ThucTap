package com.EduQuiz.Project_intel.service;

import com.EduQuiz.Project_intel.model.Exam;
import com.EduQuiz.Project_intel.model.ExamAnswerOption;
import com.EduQuiz.Project_intel.model.ExamQuestionItem;
import com.EduQuiz.Project_intel.repository.ExamQuestionItemRepository;
import com.EduQuiz.Project_intel.repository.ExamRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExamQuestionItemService {

    private final ExamQuestionItemRepository repo;
    private final ExamRepository examRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ExamQuestionItemService(ExamQuestionItemRepository repo, ExamRepository examRepository) {
        this.repo = repo;
        this.examRepository = examRepository;
    }

    public List<ExamQuestionItem> getByExam(Long examId) {
        return repo.findByExamIdOrderByOrderIndexAsc(examId);
    }

    @Transactional
    public void replaceFromJson(Long examId, String questionsJson) {
        repo.deleteByExamId(examId);

        if (questionsJson == null || questionsJson.isBlank() || questionsJson.equals("[]")) return;

        Exam examRef = examRepository.getReferenceById(examId);

        try {
            List<QuestionPayload> payloads = objectMapper.readValue(
                    questionsJson, new TypeReference<List<QuestionPayload>>() {}
            );

            for (QuestionPayload p : payloads) {
                if (p.content == null || p.content.isBlank()) continue;

                ExamQuestionItem q = new ExamQuestionItem();
                q.setExam(examRef);
                q.setOrderIndex(p.orderIndex == null ? 0 : p.orderIndex);
                q.setType(p.type == null ? "single_choice" : p.type);
                q.setContent(p.content);
                q.setScore(p.score == null ? 1.0 : p.score);

                if (p.answers != null) {
                    for (int i = 0; i < p.answers.size(); i++) {
                        AnswerPayload ap = p.answers.get(i);
                        if (ap == null) continue;

                        ExamAnswerOption opt = new ExamAnswerOption();
                        opt.setQuestion(q);
                        opt.setOrderIndex(i);
                        opt.setContent(ap.content == null ? "" : ap.content);
                        opt.setAttachmentUrl(ap.attachmentUrl);
                        opt.setCorrect(p.correctIndex != null && p.correctIndex == i);

                        q.getOptions().add(opt);
                    }
                }

                repo.save(q);
            }
        } catch (Exception e) {
            throw new RuntimeException("Parse questionsJson failed: " + e.getMessage(), e);
        }
    }

    // DTO nội bộ (đỡ phải tạo file dto)
    public static class QuestionPayload {
        public Integer orderIndex;
        public String type;
        public String content;
        public Double score;
        public Integer correctIndex;
        public List<AnswerPayload> answers;
    }
    public static class AnswerPayload {
        public String content;
        public String attachmentUrl;
    }
    public void deleteByExamId(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByExamId'");
    }
}
