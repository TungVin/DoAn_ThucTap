package com.EduQuiz.Project_intel.repository;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.EduQuiz.Project_intel.model.Exam;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    // Tìm bài kiểm tra theo trạng thái
    List<Exam> findByStatus(String status);

    // Tìm tất cả bài kiểm tra theo thứ tự ngày tạo giảm dần
    List<Exam> findAllByOrderByCreatedAtDesc();

    // Tìm bài kiểm tra công khai
    List<Exam> findByIsPublic(Boolean isPublic);

    // Tìm bài kiểm tra công khai và theo trạng thái
    List<Exam> findByIsPublicAndStatus(Boolean isPublic, String status);

    // Tìm bài kiểm tra công khai theo ngày tạo giảm dần
    List<Exam> findByIsPublicOrderByCreatedAtDesc(Boolean isPublic);
}
