package edu.polytech.plateformefilms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Ce DTO représente ce que le client (le Front) envoie
 * lorsqu'il veut poster une critique.
 */
public record ReviewRequest(
        @NotNull(message = "L'ID du film est obligatoire")
        Long movieId,

        @NotBlank(message = "Le contenu de la critique ne peut pas être vide")
        String content
) {}