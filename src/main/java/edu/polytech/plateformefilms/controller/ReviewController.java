package edu.polytech.plateformefilms.controller;

import edu.polytech.plateformefilms.model.Review;
import edu.polytech.plateformefilms.service.ReviewService;
import edu.polytech.plateformefilms.service.UserService;
import edu.polytech.plateformefilms.dto.ReviewResponse;
import edu.polytech.plateformefilms.dto.ReviewRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "Reviews", description = "Gestion des critiques de films")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    public ReviewController(ReviewService reviewService, UserService userService) {
        this.reviewService = reviewService;
        this.userService = userService;
    }

    // Création d'un avis
    @PostMapping("/{id}/reviews")
    @Operation(summary = "Créer une nouvelle critique", description = "Permet à un utilisateur de poster un avis sur un film.")
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequest request,
            Authentication authentication
    ) {
        var user = userService.findByUsername(authentication.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non trouvé");
        }

        Review newReview;
        try {
            newReview = reviewService.createReview(
                    id,
                    user.getId(),
                    request.content()
            );
        } catch (RuntimeException e) {
            String msg = e.getMessage() == null ? "" : e.getMessage();
            if (msg.contains("déjà")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, msg);
            }
            if (msg.contains("Film non trouvé")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, msg);
            }
            throw e;
        }

        return new ResponseEntity<>(convertToDto(newReview), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/reviews/{reviewId}")
    @Operation(summary = "Modifier sa critique", description = "Permet à l'auteur de modifier le contenu de sa critique pour un film.")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long id,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequest request,
            Authentication authentication
    ) {
        var user = userService.findByUsername(authentication.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non trouvé");
        }

        try {
            Review updated = reviewService.updateReview(id, reviewId, user.getId(), request.content());
            return ResponseEntity.ok(convertToDto(updated));
        } catch (RuntimeException e) {
            String msg = e.getMessage() == null ? "" : e.getMessage();
            if (msg.contains("Interdit")) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, msg);
            }
            if (msg.contains("introuvable") || msg.contains("Film non trouvé")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, msg);
            }
            throw e;
        }
    }

    @GetMapping("/{id}/reviews")
    @Operation(summary = "Lister les critiques d'un film", description = "Récupère tous les avis postés pour un ID de film donné.")
    public ResponseEntity<List<ReviewResponse>> getReviewsByMovie(@PathVariable Long id) {
        List<Review> reviews = reviewService.getReviewsByMovie(id);

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
    @DeleteMapping("/{id}/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long id,
            @PathVariable Long reviewId,
            org.springframework.security.core.Authentication authentication
    ) {
        var user = userService.findByUsername(authentication.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non trouvé");
        }

        try {
            Review review = reviewService.getReviewById(reviewId);
            if (review.getMovie() == null || !id.equals(review.getMovie().getId())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Critique introuvable");
            }
            reviewService.deleteReview(reviewId, user.getId());
        } catch (ResponseStatusException e) {
            throw e;
        } catch (RuntimeException e) {
            String msg = e.getMessage() == null ? "" : e.getMessage();
            if (msg.contains("Interdit")) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, msg);
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, msg);
        }
        return ResponseEntity.noContent().build();
    }
}