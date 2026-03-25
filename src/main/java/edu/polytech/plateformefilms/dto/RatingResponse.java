package edu.polytech.plateformefilms.dto;

public record RatingResponse(
        Long id,
        String username,
        Integer score
) {}