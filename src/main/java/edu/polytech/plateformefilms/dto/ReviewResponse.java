package edu.polytech.plateformefilms.dto;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        String username,
        String content,
        LocalDateTime createdAt
) {}