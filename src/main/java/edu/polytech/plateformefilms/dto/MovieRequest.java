package edu.polytech.plateformefilms.dto;

public record MovieRequest(String title, String director, Integer releaseYear, String genre, String synopsis) {
}
