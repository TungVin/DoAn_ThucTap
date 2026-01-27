package com.EduQuiz.Project_intel.repository;

import com.EduQuiz.Project_intel.entity.LienHe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LienHeRepository extends JpaRepository<LienHe, Long> {
}
