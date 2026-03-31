package edu.polytech.plateformefilms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MovieRequest(
        @NotBlank(message = "Le titre est obligatoire")
        String title,

        @NotBlank(message = "Le réalisateur est obligatoire")
        String director,

        @NotNull(message = "L'année de sortie est obligatoire")
        Integer releaseYear,

        @NotBlank(message = "Le genre est obligatoire")
        String genre,

        String synopsis
) {}
