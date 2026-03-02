package com.EduQuiz.Project_intel.repository;

import com.EduQuiz.Project_intel.model.ClassEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClassEnrollmentRepository extends JpaRepository<ClassEnrollment, Long> {

    boolean existsByClassRoom_IdAndStudent_Id(Long classRoomId, Long studentId);

    long countByClassRoom_Id(Long classRoomId);

    List<ClassEnrollment> findByStudent_Id(Long studentId);

    @Query("select e.classRoom.id, count(e) from ClassEnrollment e where e.classRoom.id in :classIds group by e.classRoom.id")
    List<Object[]> countMembersByClassIds(@Param("classIds") List<Long> classIds);
}
