package com.EduQuiz.Project_intel.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EduQuiz.Project_intel.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
}
