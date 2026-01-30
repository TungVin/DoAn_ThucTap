package com.EduQuiz.Project_intel.repository;

import com.EduQuiz.Project_intel.model.ClassRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClassRoomRepository extends JpaRepository<ClassRoom, Long> {

    /**
     * Tìm lớp học theo tên (không phân biệt chữ hoa/thường).
     * 
     * @param name Tên lớp học cần tìm
     * @return Danh sách các lớp học có tên chứa chuỗi tìm kiếm.
     */
    List<ClassRoom> findByNameContainingIgnoreCase(String name);

    /**
     * Kiểm tra xem lớp học có tồn tại hay không dựa trên ID.
     * 
     * @param id ID của lớp học cần kiểm tra
     * @return true nếu lớp học tồn tại, false nếu không.
     */
    boolean existsById(Long id);
}
