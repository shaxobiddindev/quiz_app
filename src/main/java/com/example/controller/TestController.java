package com.example.controller;

import com.example.payload.TestDTO;
import com.example.repository.QuizRepository;
import com.example.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
    private final TestService testService;
    private final QuizRepository quizRepository;
    @GetMapping
    public ResponseEntity<?> test(@RequestParam Integer count) {
        System.out.println(quizRepository.count());
        if (count == null || count < 1 || count < quizRepository.count()) {}
        List<TestDTO> testDTOS = testService.randomTest(count);
        return ResponseEntity.ok(testDTOS);
    }
}
