package edu.polytech.plateformefilms.service;

import edu.polytech.plateformefilms.model.Movie;
import edu.polytech.plateformefilms.model.Review;
import edu.polytech.plateformefilms.model.User;
import edu.polytech.plateformefilms.repository.MovieRepo;
import edu.polytech.plateformefilms.repository.ReviewRepo;
import edu.polytech.plateformefilms.repository.UserRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepo reviewRepo;
    private final MovieRepo movieRepo;
    private final UserRepo userRepo;

    public ReviewService(ReviewRepo reviewRepo, MovieRepo movieRepo, UserRepo userRepo) {
        this.reviewRepo = reviewRepo;
        this.movieRepo = movieRepo;
        this.userRepo = userRepo;
    }

    // Méthodes

    public List<Review> getReviewsByMovie(Long movieId) {
        Movie movie = movieRepo.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Film non trouvé"));
        return reviewRepo.findByMovie(movie);
    }

    public List<Review> getReviewsByUser(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return reviewRepo.findByUser(user);
    }

    public Review getReviewById(Long reviewId) {
        return reviewRepo.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Critique introuvable"));
    }


    public Review createReview(Long movieId, Long userId, String content) {
        // On récupère les objets
        Movie movie = movieRepo.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Film non trouvé"));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Erreur : L'utilisateur avec l'ID " + userId + " n'existe pas."));

        if (reviewRepo.findByUserAndMovie(user, movie).isPresent()) {
            throw new RuntimeException("Une critique existe déjà pour ce film");
        }

        // On crée et remplit la review
        Review review = new Review();

        review.setMovie(movie);
        review.setUser(user);
        review.setContent(content);

        return reviewRepo.save(review);
    }

    public Review updateReview(Long movieId, Long reviewId, Long userId, String content) {
        Review review = getReviewById(reviewId);
        if (review.getMovie() == null || !movieId.equals(review.getMovie().getId())) {
            throw new RuntimeException("Critique introuvable");
        }
        if (review.getUser() == null || !userId.equals(review.getUser().getId())) {
            throw new RuntimeException("Interdit : Vous n'êtes pas l'auteur de cette critique !");
        }
        review.setContent(content);
        return reviewRepo.save(review);
    }


    public void deleteReview(Long reviewId, Long userId) {
        // On récupère la critique
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Critique introuvable"));

        // On vérifie que la personne qui supprime est l'auteure de la review
        if (userId.equals(review.getUser().getId())) {
            reviewRepo.delete(review);
        } else {
            throw new RuntimeException("Interdit : Vous n'êtes pas l'auteur de cette critique !");
        }
    }
}
