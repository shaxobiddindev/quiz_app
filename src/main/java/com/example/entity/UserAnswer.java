package com.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer quizNum;
    @ManyToOne
    private Quiz quiz;
    private String answer;
    private String correctAnswer;
    private Boolean isCorrect;
    @ManyToOne
    private Test test;
    @ManyToOne
    private User user;

}
