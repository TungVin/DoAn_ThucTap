package com.EduQuiz.Project_intel.repository;

import com.EduQuiz.Project_intel.model.ExamAttempt;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {

    // Danh sách lịch sử của 1 học sinh (mới nhất -> cũ nhất)
    @EntityGraph(attributePaths = {"student", "exam"})
    List<ExamAttempt> findByStudentIdOrderBySubmittedAtDesc(Long studentId);

    // Lấy chi tiết 1 lần làm (đảm bảo đúng học sinh đang đăng nhập)
    @EntityGraph(attributePaths = {"student", "exam"})
    Optional<ExamAttempt> findByIdAndStudentId(Long id, Long studentId);

    // Danh sách sinh viên đã làm 1 bài kiểm tra (mới nhất -> cũ nhất)
    @EntityGraph(attributePaths = {"student", "exam"})
    List<ExamAttempt> findByExamIdOrderBySubmittedAtDesc(Long examId);

    // Lấy các lần làm của 1 học sinh theo từng bài kiểm tra
    @EntityGraph(attributePaths = {"student", "exam"})
    List<ExamAttempt> findByExamIdAndStudentIdOrderBySubmittedAtDesc(Long examId, Long studentId);

    // Đếm số lượt làm của 1 bài kiểm tra
    long countByExamId(Long examId);
}