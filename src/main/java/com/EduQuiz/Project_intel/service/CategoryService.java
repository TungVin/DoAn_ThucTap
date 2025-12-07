package com.EduQuiz.Project_intel.service;

import com.EduQuiz.Project_intel.model.Category;
import com.EduQuiz.Project_intel.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category create(String name, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không được để trống");
        }
        categoryRepository.findByName(name.trim()).ifPresent(c -> {
            throw new IllegalArgumentException("Danh mục đã tồn tại");
        });
        Category c = new Category();
        c.setName(name.trim());
        c.setDescription(description);
        return categoryRepository.save(c);
    }
}

