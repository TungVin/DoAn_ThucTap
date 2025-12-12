package com.EduQuiz.Project_intel.service;

import com.EduQuiz.Project_intel.model.ClassRoom;
import com.EduQuiz.Project_intel.repository.ClassRoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassRoomService {

    private final ClassRoomRepository classRoomRepository;

    public ClassRoomService(ClassRoomRepository classRoomRepository) {
        this.classRoomRepository = classRoomRepository;
    }

    public List<ClassRoom> findAll() {
        return classRoomRepository.findAll();
    }

    public ClassRoom save(ClassRoom classRoom) {
        return classRoomRepository.save(classRoom);
    }
}

