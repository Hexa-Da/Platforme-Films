package edu.polytech.plateformefilms.service;

import edu.polytech.plateformefilms.exception.DuplicateException;
import edu.polytech.plateformefilms.exception.ForbiddenException;
import edu.polytech.plateformefilms.exception.NotFoundException;
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

    public List<Review> getReviewsByMovie(Long movieId) {
        Movie movie = movieRepo.findById(movieId)
                .orElseThrow(() -> new NotFoundException("Film non trouvé"));
        return reviewRepo.findByMovie(movie);
    }

    public List<Review> getReviewsByUser(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));
        return reviewRepo.findByUser(user);
    }

    public Review getReviewById(Long reviewId) {
        return reviewRepo.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Critique introuvable"));
    }

    public Review createReview(Long movieId, Long userId, String content) {
        Movie movie = movieRepo.findById(movieId)
                .orElseThrow(() -> new NotFoundException("Film non trouvé"));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        if (reviewRepo.findByUserAndMovie(user, movie).isPresent()) {
            throw new DuplicateException("Une critique existe déjà pour ce film");
        }

        Review review = new Review();
        review.setMovie(movie);
        review.setUser(user);
        review.setContent(content);
        return reviewRepo.save(review);
    }

    public Review updateReview(Long movieId, Long reviewId, Long userId, String content) {
        Review review = getReviewById(reviewId);
        if (review.getMovie() == null || !movieId.equals(review.getMovie().getId())) {
            throw new NotFoundException("Critique introuvable");
        }
        if (review.getUser() == null || !userId.equals(review.getUser().getId())) {
            throw new ForbiddenException("Vous n'êtes pas l'auteur de cette critique");
        }
        review.setContent(content);
        return reviewRepo.save(review);
    }

    public Review updateMyReview(Long movieId, Long userId, String content) {
        Movie movie = movieRepo.findById(movieId)
                .orElseThrow(() -> new NotFoundException("Film non trouvé"));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));
        Review review = reviewRepo.findByUserAndMovie(user, movie)
                .orElseThrow(() -> new NotFoundException("Critique introuvable"));
        review.setContent(content);
        return reviewRepo.save(review);
    }

    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Critique introuvable"));
        if (!userId.equals(review.getUser().getId())) {
            throw new ForbiddenException("Vous n'êtes pas l'auteur de cette critique");
        }
        reviewRepo.delete(review);
    }
}
