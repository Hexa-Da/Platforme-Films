package edu.polytech.plateformefilms.controller;

import edu.polytech.plateformefilms.dto.RatingRequest;
import edu.polytech.plateformefilms.dto.RatingResponse;
import edu.polytech.plateformefilms.model.Rating;
import edu.polytech.plateformefilms.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ratings")
@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "Ratings", description = "Gestion des notes (1 à 5)")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    // Noter un film
    @PostMapping
    @Operation(summary = "Noter un film", description = "Ajoute une note ou met à jour la note existante de l'utilisateur pour ce film.")
    public ResponseEntity<RatingResponse> rateMovie(@RequestBody RatingRequest request) {
        // Le service gère si c'est une création ou une mise à jour
        Rating rating = ratingService.rateMovie(
                request.movieId(),
                request.userId(),
                request.score()
        );
        return new ResponseEntity<>(convertToDto(rating), HttpStatus.CREATED);
    }

    // Récupérer la note moyenne d'un film
    @GetMapping("/movie/{movieId}/average")
    @Operation(summary = "Voir la moyenne d'un film", description = "Calcule la note moyenne (Double) à partir de tous les avis.")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long movieId) {
        return ResponseEntity.ok(ratingService.getAverageRating(movieId));
    }

    // Récupérer toutes les notes d'un film
    @GetMapping("/movie/{movieId}")
    @Operation(summary = "Renvoyer toutes les critiques d'un film", description = "Renvoie toutes les critiques associées à un film par movieId")
    public ResponseEntity<List<RatingResponse>> getRatingsByMovie(@PathVariable Long movieId) {
        List<Rating> ratings = ratingService.getRatingsByMovie(movieId);
        List<RatingResponse> response = ratings.stream()
                .map(this::convertToDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    // Méthode utilitaire
    private RatingResponse convertToDto(Rating rating) {
        return new RatingResponse(
                rating.getId(),
                rating.getUser().getUsername(),
                rating.getScore()
        );
    }
}