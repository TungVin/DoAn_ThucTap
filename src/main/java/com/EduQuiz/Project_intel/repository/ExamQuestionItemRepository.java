package com.EduQuiz.Project_intel.repository;

import com.EduQuiz.Project_intel.model.ExamQuestionItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamQuestionItemRepository extends JpaRepository<ExamQuestionItem, Long> {
    List<ExamQuestionItem> findByExamIdOrderByOrderIndexAsc(Long examId);
    void deleteByExamId(Long examId);
}
