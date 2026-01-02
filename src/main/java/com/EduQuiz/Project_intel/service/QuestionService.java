package com.EduQuiz.Project_intel.service;

import com.EduQuiz.Project_intel.model.Category;
import com.EduQuiz.Project_intel.model.Question;
import com.EduQuiz.Project_intel.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public List<Question> findAllOrdered() {
        return questionRepository.findAllByOrderByCreatedAtDesc();
    }

    public Question findById(Long id) {
        return questionRepository.findById(id).orElse(null);
    }

    public Question save(Question question) {
        return questionRepository.save(question);
    }

    public void deleteById(Long id) {
        questionRepository.deleteById(id);
    }

    public Question createQuestion(String title, String content, String type, Category category,
                                   String imagePath, String audioPath, String videoPath) {
        Question q = new Question();
        q.setTitle(title);
        q.setContent(content);
        q.setType(type);
        q.setCategory(category);
        q.setImagePath(imagePath);
        q.setAudioPath(audioPath);
        q.setVideoPath(videoPath);
        return questionRepository.save(q);
    }
}
