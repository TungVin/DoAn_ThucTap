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

import java.util.ArrayList;
import java.util.List;

@Service
public class ExamQuestionItemService {

    private final ExamQuestionItemRepository repo;
    private final ExamRepository examRepository;
    private final ObjectMapper objectMapper;

    public ExamQuestionItemService(ExamQuestionItemRepository repo,
                                  ExamRepository examRepository,
                                  ObjectMapper objectMapper) {
        this.repo = repo;
        this.examRepository = examRepository;
        this.objectMapper = objectMapper;
    }

    public List<ExamQuestionItem> getByExam(Long examId) {
        return repo.findByExamIdOrderByOrderIndexAsc(examId);
    }

    /**
     * Lấy danh sách câu hỏi + đáp án của 1 bài kiểm tra dưới dạng JSON,
     * dùng để đổ vào exam-editor khi bấm "Chỉnh sửa".
     */
    @Transactional(readOnly = true)
    public String getQuestionsJsonByExamId(Long examId) {
        try {
            List<ExamQuestionItem> items = repo.findByExamIdOrderByOrderIndexAsc(examId);

            List<QuestionPayload> payloads = new ArrayList<>();
            for (ExamQuestionItem q : items) {
                QuestionPayload p = new QuestionPayload();
                p.orderIndex = q.getOrderIndex();
                p.type = q.getType();
                p.content = q.getContent();
                p.score = q.getScore();

                p.answers = new ArrayList<>();
                Integer correctIndex = null;

                // options thường đã @OrderBy("orderIndex ASC") trong entity
                List<ExamAnswerOption> opts = q.getOptions();
                if (opts != null) {
                    for (int i = 0; i < opts.size(); i++) {
                        ExamAnswerOption o = opts.get(i);

                        AnswerPayload ap = new AnswerPayload();
                        ap.content = o.getContent();
                        ap.attachmentUrl = o.getAttachmentUrl();
                        p.answers.add(ap);

                        if (Boolean.TRUE.equals(o.getCorrect())) {
                            correctIndex = i; // UI của bạn đang chọn 1 đáp án đúng
                        }
                    }
                }

                p.correctIndex = correctIndex;
                payloads.add(p);
            }

            return objectMapper.writeValueAsString(payloads);
        } catch (Exception e) {
            return "[]";
        }
    }

    /**
     * Thay thế toàn bộ câu hỏi của 1 exam bằng JSON gửi từ UI.
     * Quan trọng: KHÔNG xóa trước khi parse/validate để tránh mất dữ liệu.
     */
    @Transactional
    public void replaceFromJson(Long examId, String questionsJson) {
        if (questionsJson == null || questionsJson.isBlank()) {
            return; // không làm gì
        }

        String trimmed = questionsJson.trim();

        // Nếu UI cố tình gửi [] nghĩa là "xóa hết câu hỏi"
        if ("[]".equals(trimmed)) {
            repo.deleteByExamId(examId);
            return;
        }

        // Parse trước
        final List<QuestionPayload> payloads;
        try {
            payloads = objectMapper.readValue(trimmed, new TypeReference<List<QuestionPayload>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Parse questionsJson failed: " + e.getMessage(), e);
        }

        // Lọc câu hỏi hợp lệ (có nội dung)
        List<QuestionPayload> valid = new ArrayList<>();
        if (payloads != null) {
            for (QuestionPayload p : payloads) {
                if (p == null) continue;
                if (p.content == null || p.content.isBlank()) continue;
                valid.add(p);
            }
        }

        // Không có câu hợp lệ => KHÔNG xóa dữ liệu cũ
        if (valid.isEmpty()) {
            return;
        }

        // Từ đây mới xóa và ghi lại
        repo.deleteByExamId(examId);

        Exam examRef = examRepository.getReferenceById(examId);

        for (int idx = 0; idx < valid.size(); idx++) {
            QuestionPayload p = valid.get(idx);

            ExamQuestionItem q = new ExamQuestionItem();
            q.setExam(examRef);

            int order = (p.orderIndex != null) ? p.orderIndex : idx;
            q.setOrderIndex(order);

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
    }

    public void deleteByExamId(Long examId) {
        repo.deleteByExamId(examId);
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
}
