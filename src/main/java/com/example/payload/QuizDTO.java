package com.example.payload;

import com.example.entity.Answer;

public record QuizDTO(String question, AnswerDTO a, AnswerDTO b, AnswerDTO c, AnswerDTO d) {
}
