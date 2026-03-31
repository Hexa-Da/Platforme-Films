package edu.polytech.plateformefilms.controller;

import edu.polytech.plateformefilms.dto.RatingRequest;
import edu.polytech.plateformefilms.dto.RatingResponse;
import edu.polytech.plateformefilms.model.Rating;
import edu.polytech.plateformefilms.service.RatingService;
import edu.polytech.plateformefilms.service.UserService;
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
@Tag(name = "Ratings", description = "Gestion des notes (1 à 5)")
public class RatingController {

    private final RatingService ratingService;
    private final UserService userService;

    public RatingController(RatingService ratingService, UserService userService) {
        this.ratingService = ratingService;
        this.userService = userService;
    }

    // Noter un film
    @PostMapping("/{id}/ratings")
    @Operation(summary = "Noter un film", description = "Ajoute une note. Retourne 409 si l'utilisateur a déjà noté ce film.")
    public ResponseEntity<RatingResponse> rateMovie(
            @PathVariable Long id,
            @Valid @RequestBody RatingRequest request,
            Authentication authentication
    ) {
        var user = userService.findByUsername(authentication.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non trouvé");
        }

        Rating rating;
        try {
            rating = ratingService.createRating(id, user.getId(), request.score());
        } catch (RuntimeException e) {
            String msg = e.getMessage() == null ? "" : e.getMessage();
            if (msg.contains("déjà")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, msg);
            }
            if (msg.contains("Film non trouvé")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, msg);
            }
            if (msg.contains("Utilisateur non trouvé")) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, msg);
            }
            throw e;
        }

        return new ResponseEntity<>(convertToDto(rating), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/ratings/{ratingId}")
    @Operation(summary = "Modifier sa note", description = "Permet à l'auteur de modifier sa note pour un film.")
    public ResponseEntity<RatingResponse> updateRating(
            @PathVariable Long id,
            @PathVariable Long ratingId,
            @Valid @RequestBody RatingRequest request,
            Authentication authentication
    ) {
        var user = userService.findByUsername(authentication.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non trouvé");
        }

        try {
            Rating updated = ratingService.updateRating(id, ratingId, user.getId(), request.score());
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

    // Récupérer la note moyenne d'un film
    @GetMapping("/{id}/ratings/average")
    @Operation(summary = "Voir la moyenne d'un film", description = "Calcule la note moyenne (Double) à partir de tous les avis.")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long id) {
        return ResponseEntity.ok(ratingService.getAverageRating(id));
    }

    // Récupérer toutes les notes d'un film
    @GetMapping("/{id}/ratings")
    @Operation(summary = "Renvoyer toutes les notes d'un film", description = "Renvoie toutes les notes associées à un film par son ID")
    public ResponseEntity<List<RatingResponse>> getRatingsByMovie(@PathVariable Long id) {
        List<Rating> ratings = ratingService.getRatingsByMovie(id);
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