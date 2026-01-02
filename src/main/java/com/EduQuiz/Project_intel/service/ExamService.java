package com.EduQuiz.Project_intel.service;

import com.EduQuiz.Project_intel.model.Category;
import com.EduQuiz.Project_intel.model.Exam;
import com.EduQuiz.Project_intel.repository.ExamRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExamService {

    private final ExamRepository examRepository;

    public ExamService(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    public List<Exam> findAll() {
        return examRepository.findAllByOrderByCreatedAtDesc();
    }

    public Exam findById(Long id) {
        return examRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        examRepository.deleteById(id);
    }

    public List<Exam> findByStatus(String status) {
        return examRepository.findByStatus(status);
    }

    public Exam save(Exam exam) {
        if (exam.getTitle() == null || exam.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên bài kiểm tra không được để trống");
        }

        if (exam.getStartTime() != null && exam.getEndTime() != null) {
            if (!exam.getEndTime().isAfter(exam.getStartTime())) {
                throw new IllegalArgumentException("Thời gian kết thúc phải sau thời gian bắt đầu");
            }
            // Tự động cập nhật status dựa trên thời gian
            exam.updateStatus();
        }

        return examRepository.save(exam);
    }

    public Exam createExam(String title, String description, Category category,
                          Integer timeLimit, LocalDateTime startTime, LocalDateTime endTime) {
        Exam exam = new Exam();
        exam.setTitle(title);
        exam.setDescription(description);
        exam.setCategory(category);
        exam.setTimeLimit(timeLimit);
        exam.setStartTime(startTime);
        exam.setEndTime(endTime);
        exam.setStatus("draft");

        // Tự động cập nhật status nếu có thời gian
        if (startTime != null && endTime != null) {
            exam.updateStatus();
        }

        return save(exam);
    }
}

