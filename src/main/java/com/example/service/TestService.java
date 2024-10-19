package com.example.service;

import com.example.entity.Answer;
import com.example.entity.Quiz;
import com.example.payload.TestDTO;
import com.example.repository.AnswerRepository;
import com.example.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class TestService {
    private final QuizRepository quizRepository;
    private final AnswerRepository answerRepository;
    private final Random random = new Random();

    public List<TestDTO> randomTest(Integer count) {
        List<TestDTO> tests = new ArrayList<>();
        List<Quiz> allQuiz = quizRepository.findAll();
        List<Integer> randomQuiz = randomQuiz(allQuiz.size());
        for (int i = 0; i < count; i++) {
            List<Integer> randomAnswer = randomAnswer();
            Quiz quiz = allQuiz.get(randomQuiz.get(i));
            List<Answer> allAnswers = answerRepository.findAllByQuiz(quiz);
            TestDTO testDTO = new TestDTO(
                    quiz.getId(),
                    quiz.getQuestion(),
                    allAnswers.get(randomAnswer.get(0)).getAnswer(),
                    allAnswers.get(randomAnswer.get(1)).getAnswer(),
                    allAnswers.get(randomAnswer.get(2)).getAnswer(),
                    allAnswers.get(randomAnswer.get(3)).getAnswer());
            tests.add(testDTO);
        }
        return tests;
    }

    private List<Integer> randomQuiz(Integer count){
        List<Integer> list = new ArrayList<>();
        while(list.size() < count){
            int rand = random.nextInt(0, count);
            if (!list.contains(rand)) {
                list.add(rand);
            }
        }
        return list;
    }

    private List<Integer> randomAnswer(){
        List<Integer> list = new ArrayList<>();
        while(list.size() < 4){
            int rand = random.nextInt(0, 4);
            if (!list.contains(rand)) {
                list.add(rand);
            }
        }
        return list;
    }
}
