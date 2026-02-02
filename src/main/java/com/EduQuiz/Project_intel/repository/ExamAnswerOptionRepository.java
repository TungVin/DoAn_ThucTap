package com.EduQuiz.Project_intel.repository;

import com.EduQuiz.Project_intel.model.ExamAnswerOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExamAnswerOptionRepository extends JpaRepository<ExamAnswerOption, Long> {

    @Modifying
    @Query("delete from ExamAnswerOption o where o.question.exam.id = :examId")
    int deleteByExamId(@Param("examId") Long examId);
}
