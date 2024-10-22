package com.example.payload;

public record ScoreDTO(Long tesId, Integer total, Integer correct, Integer incorrect, String percentage) {
}
