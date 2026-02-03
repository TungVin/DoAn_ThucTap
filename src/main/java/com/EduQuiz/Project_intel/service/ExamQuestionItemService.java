package com.EduQuiz.Project_intel.service;

import com.EduQuiz.Project_intel.model.Exam;
import com.EduQuiz.Project_intel.model.ExamAnswerOption;
import com.EduQuiz.Project_intel.model.ExamQuestionItem;
import com.EduQuiz.Project_intel.repository.ExamAnswerOptionRepository;
import com.EduQuiz.Project_intel.repository.ExamQuestionItemRepository;
import com.EduQuiz.Project_intel.repository.ExamRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExamQuestionItemService {

    private final ExamQuestionItemRepository repo;
    private final ExamAnswerOptionRepository optionRepo;
    private final ExamRepository examRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ExamQuestionItemService(ExamQuestionItemRepository repo,
                                   ExamAnswerOptionRepository optionRepo,
                                   ExamRepository examRepository) {
        this.repo = repo;
        this.optionRepo = optionRepo;
        this.examRepository = examRepository;
    }

    

    @Transactional(readOnly = true)
    public List<ExamQuestionItem> getByExam(Long examId) {
        return repo.findByExamIdOrderByOrderIndexAsc(examId);
    }

    
    @Transactional(readOnly = true)
    public String getQuestionsJsonByExamId(Long examId) {
        List<ExamQuestionItem> items = repo.findByExamIdOrderByOrderIndexAsc(examId);
        List<QuestionPayload> payloads = new ArrayList<>();

        for (ExamQuestionItem q : items) {
            if (q == null) continue;

            QuestionPayload p = new QuestionPayload();
            p.orderIndex = q.getOrderIndex();
            p.type = (q.getType() == null || q.getType().isBlank()) ? "single_choice" : q.getType();
            p.content = q.getContent();
            p.score = (q.getScore() == null ? 1.0 : q.getScore());

            
            List<ExamAnswerOption> opts = q.getOptions();
            if (opts != null && !opts.isEmpty()) {
                p.answers = new ArrayList<>();
                p.correctIndex = -1;

                for (int i = 0; i < opts.size(); i++) {
                    ExamAnswerOption opt = opts.get(i);
                    if (opt == null) continue;

                    AnswerPayload ap = new AnswerPayload();
                    ap.content = opt.getContent();
                    ap.attachmentUrl = opt.getAttachmentUrl();
                    p.answers.add(ap);

                    if (Boolean.TRUE.equals(opt.getCorrect())) {
                        p.correctIndex = i;
                    }
                }
            } else {
                p.answers = new ArrayList<>();
                p.correctIndex = -1;
            }

            payloads.add(p);
        }

        try {
            return objectMapper.writeValueAsString(payloads);
        } catch (Exception e) {
            throw new RuntimeException("Build questionsJson failed: " + e.getMessage(), e);
        }
    }

    
    @Transactional
    public void replaceFromJson(Long examId, String questionsJson) {

        
        if (questionsJson == null || questionsJson.isBlank()) return;

        String s = questionsJson.trim();

        
        optionRepo.deleteByExamId(examId);
        repo.deleteByExamId(examId);

        
        if ("[]".equals(s)) return;

        Exam examRef = examRepository.getReferenceById(examId);

        try {
            List<QuestionPayload> payloads = objectMapper.readValue(
                    s, new TypeReference<List<QuestionPayload>>() {}
            );

            for (QuestionPayload p : payloads) {
                if (p == null || p.content == null || p.content.isBlank()) continue;

                ExamQuestionItem q = new ExamQuestionItem();
                q.setExam(examRef);
                q.setOrderIndex(p.orderIndex == null ? 0 : p.orderIndex);
                q.setType(p.type == null ? "single_choice" : p.type);
                q.setContent(p.content);
                q.setScore(p.score == null ? 1.0 : p.score);

                
                if (q.getOptions() == null) {
                    q.setOptions(new ArrayList<>());
                }

                if (p.answers != null) {
                    for (int i = 0; i < p.answers.size(); i++) {
                        AnswerPayload ap = p.answers.get(i);
                        if (ap == null) continue;

                        ExamAnswerOption opt = new ExamAnswerOption();
                        opt.setQuestion(q);
                        opt.setOrderIndex(i);
                        opt.setContent(ap.content == null ? "" : ap.content);
                        opt.setAttachmentUrl(ap.attachmentUrl);

                        Integer correctIndex = p.correctIndex;
                        opt.setCorrect(correctIndex != null && correctIndex == i);

                        q.getOptions().add(opt);
                    }
                }

                repo.save(q);
            }
        } catch (Exception e) {
            throw new RuntimeException("Parse questionsJson failed: " + e.getMessage(), e);
        }
    }

    
    @Transactional
    public void deleteByExamId(Long examId) {
        optionRepo.deleteByExamId(examId);
        repo.deleteByExamId(examId);
    }

    
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
}
