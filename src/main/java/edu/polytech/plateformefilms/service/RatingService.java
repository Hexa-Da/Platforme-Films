package edu.polytech.plateformefilms.service;

import edu.polytech.plateformefilms.model.Movie;
import edu.polytech.plateformefilms.model.Rating;
import edu.polytech.plateformefilms.model.User;
import edu.polytech.plateformefilms.repository.MovieRepo;
import edu.polytech.plateformefilms.repository.RatingRepo;
import edu.polytech.plateformefilms.repository.UserRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RatingService {

    private final RatingRepo ratingRepo;
    private final MovieRepo movieRepo;
    private final UserRepo userRepo;

    public RatingService(RatingRepo ratingRepo, MovieRepo movieRepo, UserRepo userRepo) {
        this.ratingRepo = ratingRepo;
        this.movieRepo = movieRepo;
        this.userRepo = userRepo;
    }

    // Méthodes

    public Rating rateMovie(Long movieId, Long userId, Integer score) {
        // On récupère les objets
        Movie movie = movieRepo.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Film non trouvé"));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));


        Optional<Rating> existingRatingOpt = ratingRepo.findByUserAndMovie(user, movie);

        if (existingRatingOpt.isPresent()) {
            // La note existe déjà donc on la met à jour
            Rating ratingToUpdate = existingRatingOpt.get();
            ratingToUpdate.setScore(score);
            return ratingRepo.save(ratingToUpdate);
        } else {
            // Elle n'existe encore pas

            Rating newRating = new Rating();
            newRating.setMovie(movie);
            newRating.setUser(user);
            newRating.setScore(score);

            return ratingRepo.save(newRating);
        }

    }

    // Récupérer toutes les notes d'un film
    public java.util.List<Rating> getRatingsByMovie(Long movieId) {
        Movie movie = movieRepo.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Film non trouvé"));

        return ratingRepo.findByMovie(movie);
    }

    // Calculer la moyenne des notes d'un film
    public Double getAverageRating(Long movieId) {
        List<Rating> ratings = getRatingsByMovie(movieId);

        if (ratings.isEmpty()) {
            return 0.0;
        }

        double sum = 0;
        for (Rating r : ratings) {
            sum += r.getScore();
        }

        return sum / ratings.size();
    }

}
