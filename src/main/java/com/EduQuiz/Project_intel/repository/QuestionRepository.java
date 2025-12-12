package com.EduQuiz.Project_intel.repository;

import com.EduQuiz.Project_intel.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findAllByOrderByCreatedAtDesc();
}

