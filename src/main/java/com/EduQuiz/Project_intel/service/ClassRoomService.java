package com.EduQuiz.Project_intel.service;

import com.EduQuiz.Project_intel.model.ClassRoom;
import com.EduQuiz.Project_intel.repository.ClassRoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClassRoomService {

    private final ClassRoomRepository classRoomRepository;

    public ClassRoomService(ClassRoomRepository classRoomRepository) {
        this.classRoomRepository = classRoomRepository;
    }

    // ==================== Find All ====================
    /**
     * Lấy tất cả các lớp học.
     * 
     * @return Danh sách các lớp học
     */
    public List<ClassRoom> findAll() {
        return classRoomRepository.findAll();
    }

    // ==================== Find By ID ====================
    /**
     * Tìm lớp học theo ID.
     * 
     * @param id ID lớp học
     * @return ClassRoom nếu tìm thấy, null nếu không tìm thấy
     */
    public ClassRoom findById(Long id) {
        Optional<ClassRoom> classRoom = classRoomRepository.findById(id);
        return classRoom.orElse(null); // Trả về lớp học nếu tìm thấy, ngược lại trả về null
    }

    // ==================== Save Class ====================
    /**
     * Lưu lớp học.
     * 
     * @param classRoom Đối tượng lớp học cần lưu
     * @return Lớp học đã được lưu
     */
    public ClassRoom save(ClassRoom classRoom) {
        return classRoomRepository.save(classRoom); // Lưu lớp học và trả về đối tượng lớp học đã được lưu
    }

    // ==================== Delete By ID ====================
    /**
     * Xóa lớp học theo ID.
     * 
     * @param id ID của lớp học cần xóa
     */
    @Transactional
    public void deleteById(Long id) {
        classRoomRepository.deleteById(id); // Xóa lớp học theo ID
    }

    // ==================== Additional Helper Methods ====================
    /**
     * Tìm lớp học theo tên.
     * 
     * @param name Tên lớp học
     * @return Danh sách lớp học tương ứng nếu tìm thấy, rỗng nếu không tìm thấy
     */
    public List<ClassRoom> findByName(String name) {
        return classRoomRepository.findByNameContainingIgnoreCase(name); // Tìm lớp học theo tên (không phân biệt hoa thường)
    }

    /**
     * Kiểm tra xem lớp học có tồn tại hay không.
     * 
     * @param id ID của lớp học
     * @return true nếu lớp học tồn tại, false nếu không tồn tại
     */
    public boolean existsById(Long id) {
        return classRoomRepository.existsById(id); // Kiểm tra sự tồn tại của lớp học theo ID
    }
}
