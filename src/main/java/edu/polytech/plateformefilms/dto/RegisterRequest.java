package edu.polytech.plateformefilms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Le nom d'utilisateur est obligatoire")
        @Size(min = 2, max = 50, message = "Le nom d'utilisateur doit faire entre 2 et 50 caractères")
        String username,

        @Email(message = "Format d'email invalide")
        String email,

        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 4, message = "Le mot de passe doit faire au moins 4 caractères")
        String password
) {}
