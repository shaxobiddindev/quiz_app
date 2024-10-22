package com.example.payload;

import java.util.List;

public record TestDTO(Long testId, List<OneTestDTO> tests) {
}
