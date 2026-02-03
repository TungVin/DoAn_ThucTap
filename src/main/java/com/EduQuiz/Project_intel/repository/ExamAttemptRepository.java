package com.EduQuiz.Project_intel.repository;

import com.EduQuiz.Project_intel.model.ExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {

    // Danh sách lịch sử của 1 học sinh (mới nhất -> cũ nhất)
    List<ExamAttempt> findByStudentIdOrderBySubmittedAtDesc(Long studentId);

    // Lấy chi tiết 1 lần làm (đảm bảo đúng học sinh đang đăng nhập)
    Optional<ExamAttempt> findByIdAndStudentId(Long id, Long studentId);
}
