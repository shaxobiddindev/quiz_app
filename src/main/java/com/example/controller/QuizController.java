package com.example.controller;

import com.example.entity.Answer;
import com.example.entity.Quiz;
import com.example.payload.QuizDTO;
import com.example.payload.QuizWithAnswerDTO;
import com.example.payload.ResultDTO;
import com.example.payload.TestDTO;
import com.example.repository.AnswerRepository;
import com.example.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/quiz")
@RequiredArgsConstructor
public class QuizController {
    private final QuizRepository quizRepository;
    private final AnswerRepository answerRepository;

    @GetMapping()
    public ResponseEntity<?> getQuiz() {
        List<QuizWithAnswerDTO> quizList = new ArrayList<>();
        for (Quiz quiz : quizRepository.findAll()) {
            List<Answer> allByQuiz = answerRepository.findAllByQuiz(quiz);
            QuizWithAnswerDTO testDTO = new QuizWithAnswerDTO(quiz, allByQuiz.get(0), allByQuiz.get(1), allByQuiz.get(2), allByQuiz.get(3));
            quizList.add(testDTO);
        }
        return ResponseEntity.ok(quizList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getQuizId(@PathVariable String id) {
        Optional<Quiz> optionalQuiz = quizRepository.findById(Integer.parseInt(id));
        if (optionalQuiz.isEmpty()) return ResponseEntity.badRequest().body("Berilgan id bo'yicha malumot topilmadi!");

        List<Answer> allByQuiz = answerRepository.findAllByQuiz(optionalQuiz.get());
        QuizWithAnswerDTO testDTO = new QuizWithAnswerDTO(optionalQuiz.get(), allByQuiz.get(0), allByQuiz.get(1), allByQuiz.get(2), allByQuiz.get(3));

        return ResponseEntity.ok(testDTO);
    }

    @PostMapping()
    public ResponseEntity<?> createQuiz(@RequestBody QuizDTO quizDTO) {

        Quiz quiz = Quiz.builder()
                .question(quizDTO.question())
                .build();
        int trueCount = 0;
        Answer answer = Answer.builder()
                .answer(quizDTO.a().answer())
                .isCorrect(quizDTO.a().isCorrect())
                .quiz(quiz)
                .build();
        if (answer.getIsCorrect()) trueCount++;
        Answer answer1 = Answer.builder()
                .answer(quizDTO.b().answer())
                .isCorrect(quizDTO.b().isCorrect())
                .quiz(quiz)
                .build();
        if (answer1.getIsCorrect()) trueCount++;
        Answer answer2 = Answer.builder()
                .answer(quizDTO.c().answer())
                .isCorrect(quizDTO.c().isCorrect())
                .quiz(quiz)
                .build();
        if (answer2.getIsCorrect()) trueCount++;
        Answer answer3 = Answer.builder()
                .answer(quizDTO.d().answer())
                .isCorrect(quizDTO.d().isCorrect())
                .quiz(quiz)
                .build();
        if (answer3.getIsCorrect()) trueCount++;

        if (trueCount != 1) {
            return ResponseEntity.status(400).body("Badly formatted request!");
        }

        quizRepository.save(quiz);
        answerRepository.save(answer);
        answerRepository.save(answer1);
        answerRepository.save(answer2);
        answerRepository.save(answer3);
        QuizWithAnswerDTO quizRes = new QuizWithAnswerDTO(quiz, answer, answer1, answer2, answer3);
        return ResponseEntity.ok(quizRes);
    }
}
