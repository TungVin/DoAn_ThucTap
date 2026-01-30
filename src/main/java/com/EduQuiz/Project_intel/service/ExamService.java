package com.EduQuiz.Project_intel.service;

import com.EduQuiz.Project_intel.dto.ExamCardDTO;
import com.EduQuiz.Project_intel.dto.ExamUpsertForm;
import com.EduQuiz.Project_intel.model.Category;
import com.EduQuiz.Project_intel.model.Exam;
import com.EduQuiz.Project_intel.repository.CategoryRepository;
import com.EduQuiz.Project_intel.repository.ExamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExamService {

    private final ExamRepository examRepository;
    private final CategoryRepository categoryRepository;

    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter CARD_DATE_FMT = DateTimeFormatter.ofPattern("'Thg' MM dd");

    public ExamService(ExamRepository examRepository, CategoryRepository categoryRepository) {
        this.examRepository = examRepository;
        this.categoryRepository = categoryRepository;
    }

    // ================== LIST CARD (CHO TRANG DTO) ==================

    @Transactional(readOnly = true)
    public List<ExamCardDTO> getCards() {
        return examRepository.findAll()
                .stream()
                .map(this::toCard)
                .toList();
    }

    private ExamCardDTO toCard(Exam e) {
        String title = (e.getTitle() == null || e.getTitle().isBlank()) ? "(Chưa đặt tên)" : e.getTitle();

        String timeLabel = (e.getTimeLimit() == null) ? "Không giới hạn" : (e.getTimeLimit() + " phút");

        // ưu tiên createdAt nếu có, không có thì dùng hôm nay
        String dateLabel;
        if (getCreatedAtLocalDate(e) != null) {
            dateLabel = getCreatedAtLocalDate(e).format(CARD_DATE_FMT);
        } else {
            dateLabel = LocalDate.now().format(CARD_DATE_FMT);
        }

        // statusLabel: nếu bạn chưa có status trong entity -> để mặc định
        String statusLabel = "Bản nháp";

        int questionCount = 0;
        String thumbUrl = null;

        return new ExamCardDTO(
                e.getId(),
                title,
                timeLabel,
                dateLabel,
                statusLabel,
                questionCount,
                thumbUrl
        );
    }

    /**
     * Helper: tránh lỗi nếu Exam của bạn chưa có createdAt hoặc kiểu khác.
     * Nếu entity Exam có getCreatedAt() trả về LocalDateTime thì sẽ dùng được.
     */
    private LocalDate getCreatedAtLocalDate(Exam e) {
        try {
            // nếu Exam có getCreatedAt(): LocalDateTime
            var createdAt = e.getCreatedAt();
            if (createdAt == null) return null;
            return createdAt.toLocalDate();
        } catch (Exception ex) {
            // Exam chưa có field createdAt hoặc kiểu khác
            return null;
        }
    }

    // ================== LIST ENTITY (CHO FRAGMENT NINEQUIZ ĐANG DÙNG Exam) ==================

    @Transactional(readOnly = true)
    public List<Exam> getAll() {
        return examRepository.findAll();
    }

    // ================== CREATE / UPDATE ==================

    @Transactional
    public Long createFromForm(ExamUpsertForm form) {
        Exam exam = new Exam();
        applyForm(exam, form);
        examRepository.save(exam);
        return exam.getId();
    }

    @Transactional(readOnly = true)
    public ExamUpsertForm getFormById(Long id) {
        Exam exam = examRepository.findById(id).orElseThrow();

        ExamUpsertForm f = new ExamUpsertForm();
        f.setId(exam.getId());
        f.setTitle(exam.getTitle());
        f.setDescription(exam.getDescription());

        if (exam.getCategory() != null) {
            f.setCategoryId(exam.getCategory().getId());
        }

        if (exam.getTimeLimit() != null) {
            f.setTimeLimitEnabled(true);
            f.setTimeLimit(exam.getTimeLimit());
        }

        if (exam.getStartTime() != null) {
            f.setStartEnabled(true);
            f.setStartDate(exam.getStartTime().toLocalDate().toString());
            f.setStartTime(exam.getStartTime().toLocalTime().toString().substring(0, 5));
        }

        if (exam.getEndTime() != null) {
            f.setEndEnabled(true);
            f.setEndDate(exam.getEndTime().toLocalDate().toString());
            f.setEndTime(exam.getEndTime().toLocalTime().toString().substring(0, 5));
        }

        if (exam.getResultDisplayMode() != null) {
            f.setResultDisplayMode(exam.getResultDisplayMode());
        }

        if (exam.getAutoDivideScore() != null) {
            f.setAutoDivideScore(exam.getAutoDivideScore());
        }

        if (exam.getMaxScore() != null) {
            f.setMaxScore(exam.getMaxScore());
        }

        if (exam.getMaxAttempts() != null) {
            f.setMaxAttempts(exam.getMaxAttempts());
        }

        if (exam.getQuestionNumberStyle() != null) {
            f.setQuestionNumberStyle(exam.getQuestionNumberStyle());
        }

        if (exam.getQuestionsPerPage() != null) {
            f.setQuestionsPerPage(exam.getQuestionsPerPage());
        }

        if (exam.getAnswersPerRow() != null) {
            f.setAnswersPerRow(exam.getAnswersPerRow());
        }

        // Thêm trường isPublic
        f.setIsPublic(exam.getIsPublic());

        return f;
    }

    @Transactional
    public void updateFromForm(Long id, ExamUpsertForm form) {
        Exam exam = examRepository.findById(id).orElseThrow();
        applyForm(exam, form);
        examRepository.save(exam);
    }

    // ================== DELETE ==================

    @Transactional
    public void deleteById(Long id) {
        examRepository.deleteById(id);
    }

    // ================== APPLY FORM ==================

    private void applyForm(Exam exam, ExamUpsertForm f) {
        exam.setTitle(f.getTitle());
        exam.setDescription(f.getDescription());

        if (f.getCategoryId() != null) {
            Category cat = categoryRepository.findById(f.getCategoryId()).orElseThrow();
            exam.setCategory(cat);
        } else {
            exam.setCategory(null);
        }

        if (!f.isTimeLimitEnabled()) {
            exam.setTimeLimit(null);
        } else {
            exam.setTimeLimit(f.getTimeLimit());
        }

        exam.setStartTime(parseLocalDateTime(f.isStartEnabled(), f.getStartDate(), f.getStartTime()));
        exam.setEndTime(parseLocalDateTime(f.isEndEnabled(), f.getEndDate(), f.getEndTime()));

        if (exam.getStartTime() != null && exam.getEndTime() != null
                && exam.getEndTime().isBefore(exam.getStartTime())) {
            throw new IllegalArgumentException("Thời gian kết thúc phải sau thời gian bắt đầu");
        }

        if (f.getResultDisplayMode() != null) {
            exam.setResultDisplayMode(f.getResultDisplayMode());
        } else if (exam.getResultDisplayMode() == null) {
            exam.setResultDisplayMode(Exam.ResultDisplayMode.AFTER_TEACHER_REVIEW);
        }

        exam.setAutoDivideScore(f.isAutoDivideScore());

        if (f.getMaxScore() != null) {
            exam.setMaxScore(f.getMaxScore());
        } else if (exam.getMaxScore() == null) {
            exam.setMaxScore(10);
        }

        if (f.getMaxAttempts() != null) {
            exam.setMaxAttempts(f.getMaxAttempts());
        } else if (exam.getMaxAttempts() == null) {
            exam.setMaxAttempts(1);
        }

        if (f.getQuestionNumberStyle() != null && !f.getQuestionNumberStyle().isBlank()) {
            exam.setQuestionNumberStyle(f.getQuestionNumberStyle());
        } else if (exam.getQuestionNumberStyle() == null) {
            exam.setQuestionNumberStyle("LETTER");
        }

        if (f.getQuestionsPerPage() != null) {
            exam.setQuestionsPerPage(f.getQuestionsPerPage());
        } else if (exam.getQuestionsPerPage() == null) {
            exam.setQuestionsPerPage(50);
        }

        if (f.getAnswersPerRow() != null) {
            exam.setAnswersPerRow(f.getAnswersPerRow());
        } else if (exam.getAnswersPerRow() == null) {
            exam.setAnswersPerRow(1);
        }

        // Cập nhật trạng thái công khai
        if (f.getIsPublic() != null) {
            exam.setIsPublic(f.getIsPublic());
        }
    }

    private LocalDateTime parseLocalDateTime(boolean enabled, String date, String time) {
        if (!enabled) return null;
        if (date == null || date.isBlank() || time == null || time.isBlank()) return null;
        return LocalDateTime.parse(date + "T" + time, DATE_TIME_FMT);
    }
}
