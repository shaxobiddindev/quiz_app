package com.example.service;

import com.example.entity.*;
import com.example.payload.OneTestDTO;
import com.example.payload.ResultDTO;
import com.example.payload.ScoreDTO;
import com.example.repository.AnswerRepository;
import com.example.repository.QuizRepository;
import com.example.repository.TestRepository;
import com.example.repository.UserAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TestService {
    private final QuizRepository quizRepository;
    private final AnswerRepository answerRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final TestRepository testRepository;
    private final Random random = new Random();

    public List<OneTestDTO> randomTest(Integer count, Test test) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<OneTestDTO> tests = new ArrayList<>();
        List<Quiz> allQuiz = quizRepository.findAll();

        List<Integer> randomQuiz = randomQuiz(allQuiz.size());
        for (int i = 0; i < count; i++) {
            List<Integer> randomAnswer = randomAnswer();
            Quiz quiz = allQuiz.get(randomQuiz.get(i));
            List<Answer> allAnswers = answerRepository.findAllByQuiz(quiz);
            char ans = 97;
            UserAnswer userAnswer = UserAnswer.builder()
                    .user(user)
                    .quiz(quiz)
                    .quizNum(i+1)
                    .test(test)
                    .isCorrect(false)
                    .build();
            for (Integer random:randomAnswer) {
                if (allAnswers.get(random).getIsCorrect()) {
                    userAnswer.setCorrectAnswer(Character.toString(ans));
                }
                ans++;
            }

            OneTestDTO testDTO = new OneTestDTO(
                    (long)(i+1),
                    quiz.getQuestion(),
                    allAnswers.get(randomAnswer.get(0)).getAnswer(),
                    allAnswers.get(randomAnswer.get(1)).getAnswer(),
                    allAnswers.get(randomAnswer.get(2)).getAnswer(),
                    allAnswers.get(randomAnswer.get(3)).getAnswer());
            tests.add(testDTO);
            userAnswerRepository.save(userAnswer);
        }
        return tests;
    }

    public List<OneTestDTO> unfinishedTest(Test test, User user) {
        List<OneTestDTO> tests = new ArrayList<>();
        List<UserAnswer> allQuiz = userAnswerRepository.findAllByUserAndTest(user, test);
        List<Integer> randomQuiz = randomQuiz(allQuiz.size());

        for (int i = 0; i < allQuiz.size(); i++) {
            List<Integer> randomAnswer = randomAnswer();
            Quiz quiz = allQuiz.get(randomQuiz.get(i)).getQuiz();
            List<Answer> allAnswers = answerRepository.findAllByQuiz(quiz);
            char ans = 97;

            for (Integer random:randomAnswer) {
                if (allAnswers.get(random).getIsCorrect()) {
                    allQuiz.get(i).setCorrectAnswer(Character.toString(ans));
                }
                ans++;
            }

            OneTestDTO testDTO = new OneTestDTO(
                    (long)(i+1),
                    quiz.getQuestion(),
                    allAnswers.get(randomAnswer.get(0)).getAnswer(),
                    allAnswers.get(randomAnswer.get(1)).getAnswer(),
                    allAnswers.get(randomAnswer.get(2)).getAnswer(),
                    allAnswers.get(randomAnswer.get(3)).getAnswer());
            tests.add(testDTO);
            userAnswerRepository.save(allQuiz.get(i));
        }
        return tests;
    }



    private List<Integer> randomQuiz(Integer count){
        List<Integer> list = new ArrayList<>();
        while(list.size() < count){
            int rand = random.nextInt(0, count);
            if (!list.contains(rand))
                list.add(rand);
        }
        return list;
    }

    public Optional<Test> getUnfinishedTest(User user){
        Test test = null;
        for (Test temp : testRepository.findAllByUser(user)) {
            if (temp.getCreateTime().getTime()+temp.getTime() > System.currentTimeMillis()){
                temp.setIsFinished(true);
                testRepository.save(temp);
            }

            if (test == null)test = temp;
            else if (test.getCreateTime().before(temp.getCreateTime()))test = temp;
        }
        if (test != null && (test.getCreateTime().getTime()+test.getTime()) > System.currentTimeMillis())return Optional.of(test);
        return Optional.empty();
    }

    private List<Integer> randomAnswer(){
        List<Integer> list = new ArrayList<>();
        while(list.size() < 4){
            int rand = random.nextInt(0, 4);
            if (!list.contains(rand))
                list.add(rand);
        }
        return list;
    }

    public ResponseEntity<?> checkTest(List<ResultDTO> results, Integer testId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Test> optionalTest = testRepository.findById((long) testId);
        if (optionalTest.isEmpty())return ResponseEntity.badRequest().body("Test not found!");
        Test test = optionalTest.get();
        if (test.getIsFinished())return ResponseEntity.badRequest().body("The test is finished!");
        if ((test.getCreateTime().getTime()+test.getTime()) <= System.currentTimeMillis()) {
            test.setIsFinished(true);
            testRepository.save(test);
            return ResponseEntity.badRequest().body("The test time is over!");
        }
        int count = 0;
        List<UserAnswer> userAnswers = userAnswerRepository.findAllByUserAndTest(user, test);
        for (ResultDTO resultDTO : results)
            if (check(resultDTO, test, user, userAnswers))
                 count++;
        return ResponseEntity.ok(new ScoreDTO(test.getId(), userAnswers.size(), count, userAnswers.size()-count, ((count*1.0)/userAnswers.size()*100)+"%"));
    }

    private Boolean check(ResultDTO result, Test test, User user, List<UserAnswer> userAnswers) {
        for (UserAnswer userAnswer : userAnswers)
            if (Objects.equals(userAnswer.getQuizNum(), result.quizNum())){
                userAnswer.setAnswer(result.result());
                if (userAnswer.getCorrectAnswer().equals(result.result()))userAnswer.setIsCorrect(true);
                userAnswerRepository.save(userAnswer);
                return userAnswer.getIsCorrect();
            }
        return false;
    }

    public ResponseEntity<?> userResult(User user, Long id) {
        Optional<Test> optionalTest = testRepository.findById(id);
        if (optionalTest.isEmpty() || !optionalTest.get().getUser().getId().equals(user.getId()))return ResponseEntity.badRequest().body("Id is incorrect!");
        Test test = optionalTest.get();
        return ResponseEntity.ok(createResult(test, user));
    }

    private ScoreDTO createResult(Test test, User user) {
        List<UserAnswer> userAnswers = userAnswerRepository.findAllByUserAndTest(user, test);
        List<UserAnswer> corrects = userAnswerRepository.findAllByTestAndIsCorrect(test, true);
        return new ScoreDTO(test.getId(), userAnswers.size(), corrects.size(), userAnswers.size()-corrects.size(), ((corrects.size()*1.0)/userAnswers.size()*100)+"%");
    }

    public ResponseEntity<?> userResults(User user) {
        List<Test> tests = testRepository.findAllByUser(user);
        List<ScoreDTO> results = new ArrayList<>();
        for (Test test : tests) {
            results.add(createResult(test, user));
        }
        return ResponseEntity.ok(results);
    }
}
