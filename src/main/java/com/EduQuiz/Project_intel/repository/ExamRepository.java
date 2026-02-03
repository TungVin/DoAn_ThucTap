package com.EduQuiz.Project_intel.repository;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.EduQuiz.Project_intel.model.Exam;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    // ====== LISTING (bạn đang dùng) ======
    List<Exam> findByStatus(String status);

    List<Exam> findAllByOrderByCreatedAtDesc();

    List<Exam> findByIsPublic(Boolean isPublic);

    List<Exam> findByIsPublicAndStatus(Boolean isPublic, String status);

    List<Exam> findByIsPublicOrderByCreatedAtDesc(Boolean isPublic);

    // ====== COUNT (phục vụ thống kê profile) ======
    long countByIsPublic(Boolean isPublic);

    long countByStatus(String status);

    long countByIsPublicAndStatus(Boolean isPublic, String status);

    /**
     * ✅ (Tuỳ chọn) Thống kê "bài kiểm tra của tôi" cho giáo viên
     * Chỉ dùng được khi Exam có field:
     *   @ManyToOne User createdBy;
     * và cột created_by trong DB.
     */
    long countByCreatedBy_Id(Long userId);
}
