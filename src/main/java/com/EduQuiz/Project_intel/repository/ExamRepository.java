package com.EduQuiz.Project_intel.repository;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.EduQuiz.Project_intel.model.Exam;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findByStatus(String status);
    List<Exam> findAllByOrderByCreatedAtDesc();
}


