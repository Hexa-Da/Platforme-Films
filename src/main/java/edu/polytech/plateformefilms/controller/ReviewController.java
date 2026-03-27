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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
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
    @PostMapping
    @Operation(summary = "Créer une nouvelle critique", description = "Permet à un utilisateur de poster un avis sur un film.")
    public ResponseEntity<ReviewResponse> createReview(
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal(expression = "username") String username
    ) {
        var user = userService.findByUsername(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non trouvé");
        }

        Review newReview = reviewService.createReview(
                request.movieId(),
                user.getId(),
                request.content()
        );

        return new ResponseEntity<>(convertToDto(newReview), HttpStatus.CREATED);
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
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long id,
            @AuthenticationPrincipal(expression = "username") String username
    ) {
        var user = userService.findByUsername(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non trouvé");
        }

        try {
            reviewService.deleteReview(id, user.getId());
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