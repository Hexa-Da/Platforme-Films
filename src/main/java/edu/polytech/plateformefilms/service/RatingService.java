package edu.polytech.plateformefilms.service;

import edu.polytech.plateformefilms.exception.DuplicateException;
import edu.polytech.plateformefilms.exception.ForbiddenException;
import edu.polytech.plateformefilms.exception.NotFoundException;
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

    public Rating getRatingById(Long ratingId) {
        return ratingRepo.findById(ratingId)
                .orElseThrow(() -> new NotFoundException("Note introuvable"));
    }

    public Rating createRating(Long movieId, Long userId, Integer score) {
        Movie movie = movieRepo.findById(movieId)
                .orElseThrow(() -> new NotFoundException("Film non trouvé"));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        if (ratingRepo.findByUserAndMovie(user, movie).isPresent()) {
            throw new DuplicateException("Une note existe déjà pour ce film");
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
            throw new NotFoundException("Note introuvable");
        }
        if (rating.getUser() == null || !userId.equals(rating.getUser().getId())) {
            throw new ForbiddenException("Vous n'êtes pas l'auteur de cette note");
        }
        rating.setScore(score);
        return ratingRepo.save(rating);
    }

    public Rating updateMyRating(Long movieId, Long userId, Integer score) {
        Movie movie = movieRepo.findById(movieId)
                .orElseThrow(() -> new NotFoundException("Film non trouvé"));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));
        Rating rating = ratingRepo.findByUserAndMovie(user, movie)
                .orElseThrow(() -> new NotFoundException("Note introuvable"));
        rating.setScore(score);
        return ratingRepo.save(rating);
    }

    public List<Rating> getRatingsByMovie(Long movieId) {
        Movie movie = movieRepo.findById(movieId)
                .orElseThrow(() -> new NotFoundException("Film non trouvé"));
        return ratingRepo.findByMovie(movie);
    }

    public Double getAverageRating(Long movieId) {
        List<Rating> ratings = getRatingsByMovie(movieId);
        if (ratings.isEmpty()) {
            return 0.0;
        }
        return ratings.stream().mapToInt(Rating::getScore).average().orElse(0.0);
    }
}
