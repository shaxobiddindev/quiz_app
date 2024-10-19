package com.example.repository;

import com.example.entity.Answer;
import com.example.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer,Long> {
    List<Answer> findAllByQuiz(Quiz quiz);
}
