package edu.polytech.plateformefilms.service;

import edu.polytech.plateformefilms.model.Movie;
import edu.polytech.plateformefilms.model.Rating;
import edu.polytech.plateformefilms.model.User;
import edu.polytech.plateformefilms.repository.MovieRepo;
import edu.polytech.plateformefilms.repository.RatingRepo;
import edu.polytech.plateformefilms.repository.UserRepo;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Rating getRatingById(Long ratingId) {
        return ratingRepo.findById(ratingId)
                .orElseThrow(() -> new RuntimeException("Note introuvable"));
    }

    public Rating createRating(Long movieId, Long userId, Integer score) {
        Movie movie = movieRepo.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Film non trouvé"));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (ratingRepo.findByUserAndMovie(user, movie).isPresent()) {
            throw new RuntimeException("Une note existe déjà pour ce film");
        }

        Rating newRating = new Rating();
        newRating.setMovie(movie);
        newRating.setUser(user);
        newRating.setScore(score);

        return ratingRepo.save(newRating);
    }

    public Rating updateRating(Long movieId, Long ratingId, Long userId, Integer score) {
        Rating rating = getRatingById(ratingId);
        if (rating.getMovie() == null || !movieId.equals(rating.getMovie().getId())) {
            throw new RuntimeException("Note introuvable");
        }
        if (rating.getUser() == null || !userId.equals(rating.getUser().getId())) {
            throw new RuntimeException("Interdit : Vous n'êtes pas l'auteur de cette note !");
        }
        rating.setScore(score);
        return ratingRepo.save(rating);
    }

    public Rating updateMyRating(Long movieId, Long userId, Integer score) {
        Movie movie = movieRepo.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Film non trouvé"));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Rating rating = ratingRepo.findByUserAndMovie(user, movie)
                .orElseThrow(() -> new RuntimeException("Note introuvable"));
        rating.setScore(score);
        return ratingRepo.save(rating);
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
