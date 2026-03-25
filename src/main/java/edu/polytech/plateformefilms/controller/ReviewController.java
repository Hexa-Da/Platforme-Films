package edu.polytech.plateformefilms.controller;

import edu.polytech.plateformefilms.model.Review;
import edu.polytech.plateformefilms.service.ReviewService;
import edu.polytech.plateformefilms.dto.ReviewResponse;
import edu.polytech.plateformefilms.dto.ReviewRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "Reviews", description = "Gestion des critiques de films")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // Création d'un avis
    @PostMapping
    @Operation(summary = "Créer une nouvelle critique", description = "Permet à un utilisateur de poster un avis sur un film.")
    public ResponseEntity<Review> createReview(@RequestBody ReviewRequest request) {
        Review newReview = reviewService.createReview(
                request.movieId(),
                request.userId(),
                request.content()
        );
        return new ResponseEntity<>(newReview, HttpStatus.CREATED);
    }

    @GetMapping("/movie/{movieId}")
    @Operation(summary = "Lister les critiques d'un film", description = "Récupère tous les avis postés pour un ID de film donné.")
    public ResponseEntity<List<ReviewResponse>> getReviewsByMovie(@PathVariable Long movieId) {
        List<Review> reviews = reviewService.getReviewsByMovie(movieId);

        // On transforme la liste d'entités en liste de DTOs
        List<ReviewResponse> response = reviews.stream()
                .map(this::convertToDto)
                .toList();

        return ResponseEntity.ok(response);
    }

    // Méthode utilitaire de conversion
    private ReviewResponse convertToDto(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getUser().getUsername(),
                review.getContent(),
                review.getCreatedAt()
        );
    }

    @Operation(summary = "Supprimer une critique", description = "Supprime un avis. Seul l'auteur peut effectuer cette action.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id, @RequestParam Long userId) {
        reviewService.deleteReview(id, userId);
        return ResponseEntity.noContent().build();
    }
}