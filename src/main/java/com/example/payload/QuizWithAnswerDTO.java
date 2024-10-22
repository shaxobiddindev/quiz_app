package com.example.payload;

import com.example.entity.Answer;
import com.example.entity.Quiz;

public record QuizWithAnswerDTO(Quiz quiz, Answer a, Answer b, Answer c, Answer d) {
}
