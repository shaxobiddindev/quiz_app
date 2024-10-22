package com.example.controller;

import com.example.entity.Test;
import com.example.entity.User;
import com.example.payload.OneTestDTO;

import com.example.payload.ResultDTO;
import com.example.payload.TestDTO;
import com.example.repository.QuizRepository;
import com.example.repository.TestRepository;
import com.example.service.TestService;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.LogManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tests")
@RequiredArgsConstructor
public class TestController {
    private final TestService testService;
    private final QuizRepository quizRepository;
    private final TestRepository testRepository;

    @GetMapping("/start")
    public ResponseEntity<?> test(@RequestParam Integer count) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(quizRepository.count());
        if (count == null || count < 1 || count > quizRepository.count())
            return ResponseEntity.badRequest().body("So'rovdagi miqdor bazada mavjud emas");
        Optional<Test> optionalTest = testService.getUnfinishedTest(user);
        if (optionalTest.isPresent()) {
            return ResponseEntity.ok(new TestDTO(optionalTest.get().getId(), testService.unfinishedTest(optionalTest.get(), user)));
        }
        Test test = Test.builder()
                .user(user)
                .time((count * 30000))
                .build();
        testRepository.save(test);
        List<OneTestDTO> testDTOS = testService.randomTest(count, test);
        return ResponseEntity.ok(new TestDTO(test.getId(), testDTOS));
    }

    @PostMapping()
    public ResponseEntity<?> test(@RequestBody List<ResultDTO> results, @RequestParam Integer testId) {
        if (testId == null) return ResponseEntity.badRequest().body("Test id is incorrect!");
        return testService.checkTest(results, testId);
    }

    @GetMapping
    public ResponseEntity<?> testResult() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return testService.userResults(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> testResult(@PathVariable Long id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return testService.userResult(user, id);
    }
}
