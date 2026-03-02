package com.EduQuiz.Project_intel.service;

import com.EduQuiz.Project_intel.model.ClassEnrollment;
import com.EduQuiz.Project_intel.model.ClassRoom;
import com.EduQuiz.Project_intel.model.User;
import com.EduQuiz.Project_intel.repository.ClassEnrollmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClassEnrollmentService {

    private final ClassEnrollmentRepository enrollmentRepository;

    public ClassEnrollmentService(ClassEnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    public boolean isEnrolled(Long classRoomId, Long studentId) {
        return enrollmentRepository.existsByClassRoom_IdAndStudent_Id(classRoomId, studentId);
    }

    @Transactional
    public void enroll(User student, ClassRoom classRoom) {
        if (student == null || classRoom == null) {
            throw new IllegalArgumentException("Thiếu thông tin học sinh hoặc lớp học");
        }
        if (isEnrolled(classRoom.getId(), student.getId())) {
            return;
        }
        ClassEnrollment enrollment = new ClassEnrollment();
        enrollment.setStudent(student);
        enrollment.setClassRoom(classRoom);
        enrollmentRepository.save(enrollment);
    }

    public long countMembers(Long classRoomId) {
        return enrollmentRepository.countByClassRoom_Id(classRoomId);
    }

    public Map<Long, Long> countMembersByClassIds(List<Long> classIds) {
        if (classIds == null || classIds.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, Long> result = new HashMap<>();
        for (Object[] row : enrollmentRepository.countMembersByClassIds(classIds)) {
            if (row == null || row.length < 2) continue;
            Long classId = (Long) row[0];
            Long count = (Long) row[1];
            result.put(classId, count);
        }
        return result;
    }

    public List<ClassEnrollment> findByStudentId(Long studentId) {
        return enrollmentRepository.findByStudent_Id(studentId);
    }
}
