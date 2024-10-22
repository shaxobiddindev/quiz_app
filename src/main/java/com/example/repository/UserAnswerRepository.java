package com.example.repository;

import com.example.entity.Test;
import com.example.entity.User;
import com.example.entity.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
    List<UserAnswer> findAllByTest(Test user);
    List<UserAnswer> findAllByUserAndTest(User user, Test test);
    List<UserAnswer> findAllByTestAndIsCorrect(Test user, Boolean isCorrect);
}
