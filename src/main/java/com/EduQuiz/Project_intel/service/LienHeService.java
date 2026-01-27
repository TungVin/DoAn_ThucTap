package com.EduQuiz.Project_intel.service;

import com.EduQuiz.Project_intel.entity.LienHe;
import com.EduQuiz.Project_intel.repository.LienHeRepository;
import org.springframework.stereotype.Service;

@Service
public class LienHeService {

    private final LienHeRepository lienHeRepository;

    public LienHeService(LienHeRepository lienHeRepository) {
        this.lienHeRepository = lienHeRepository;
    }

    public void save(LienHe lienHe) {
        lienHeRepository.save(lienHe);
    }
}
