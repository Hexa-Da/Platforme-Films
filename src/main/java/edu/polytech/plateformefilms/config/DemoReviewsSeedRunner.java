package edu.polytech.plateformefilms.config;

import edu.polytech.plateformefilms.model.Movie;
import edu.polytech.plateformefilms.model.Rating;
import edu.polytech.plateformefilms.model.Review;
import edu.polytech.plateformefilms.model.User;
import edu.polytech.plateformefilms.repository.MovieRepo;
import edu.polytech.plateformefilms.repository.RatingRepo;
import edu.polytech.plateformefilms.repository.ReviewRepo;
import edu.polytech.plateformefilms.repository.UserRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Données de démo : utilisateurs fictifs + critiques et notes pour tester l’UI (liste des avis sur la fiche film).
 * Désactivé en prod via {@code app.seed.demo-reviews.enabled=false}.
 */
@Configuration
public class DemoReviewsSeedRunner {

    @Bean
    @Order(2)
    CommandLineRunner demoReviewsSeed(
            MovieRepo movieRepo,
            UserRepo userRepo,
            ReviewRepo reviewRepo,
            RatingRepo ratingRepo,
            PasswordEncoder passwordEncoder,
            @Value("${app.seed.demo-reviews.enabled:true}") boolean enabled
    ) {
        return args -> {
            if (!enabled) {
                return;
            }

            User alice = ensureUser(userRepo, passwordEncoder, "demo_alice", "demo-alice@example.local", "Demo1234!");
            User bob = ensureUser(userRepo, passwordEncoder, "demo_bob", "demo-bob@example.local", "Demo1234!");
            User charlie = ensureUser(userRepo, passwordEncoder, "demo_charlie", "demo-charlie@example.local", "Demo1234!");

            findMovie(movieRepo, "Inception").ifPresent(movie -> {
                ensureRating(ratingRepo, alice, movie, 5);
                ensureReview(reviewRepo, alice, movie,
                        "Chef-d’œuvre du thriller psychologique. La bande-son et le montage sont impeccables.");
                ensureRating(ratingRepo, bob, movie, 4);
                ensureReview(reviewRepo, bob, movie,
                        "Parfois un peu dense, mais l’idée des rêves imbriqués reste brillante.");
                ensureRating(ratingRepo, charlie, movie, 5);
                ensureReview(reviewRepo, charlie, movie,
                        "À revoir plusieurs fois pour tout saisir — expérience cinéma mémorable.");

                // 17 avis supplémentaires (3 + 17 = 20) pour tester le défilement côté UI
                for (int i = 1; i <= 17; i++) {
                    String username = String.format("demo_scroll_%02d", i);
                    String email = String.format("demo-scroll-%02d@example.local", i);
                    User u = ensureUser(userRepo, passwordEncoder, username, email, "Demo1234!");
                    int score = 3 + (i % 3);
                    ensureRating(ratingRepo, u, movie, score);
                    ensureReview(reviewRepo, u, movie,
                            "Critique de test n°" + i + " — utile pour vérifier le défilement de la liste des avis sur la fiche film.");
                }
            });

            findMovie(movieRepo, "Parasite").ifPresent(movie -> {
                ensureRating(ratingRepo, alice, movie, 5);
                ensureReview(reviewRepo, alice, movie,
                        "Satire sociale très bien écrite, alterne tension et humour noir avec maîtrise.");
                ensureRating(ratingRepo, bob, movie, 5);
                ensureReview(reviewRepo, bob, movie,
                        "Palme méritée. Quelques scènes restent gravées longtemps après le générique.");
            });

            findMovie(movieRepo, "Dune").ifPresent(movie -> {
                ensureRating(ratingRepo, bob, movie, 4);
                ensureReview(reviewRepo, bob, movie,
                        "Visuellement somptueux ; j’attends la suite pour juger l’ensemble de l’adaptation.");
            });
        };
    }

    private static Optional<Movie> findMovie(MovieRepo movieRepo, String exactTitle) {
        return movieRepo.findByTitleContainingIgnoreCase(exactTitle).stream()
                .filter(m -> exactTitle.equalsIgnoreCase(m.getTitle()))
                .findFirst();
    }

    private static User ensureUser(
            UserRepo userRepo,
            PasswordEncoder passwordEncoder,
            String username,
            String email,
            String rawPassword
    ) {
        return userRepo.findByUsername(username).orElseGet(() -> {
            User u = new User();
            u.setUsername(username);
            u.setEmail(email);
            u.setPassword(passwordEncoder.encode(rawPassword));
            u.setCreatedAt(LocalDateTime.now());
            return userRepo.save(u);
        });
    }

    private static void ensureReview(ReviewRepo reviewRepo, User user, Movie movie, String content) {
        if (reviewRepo.findByUserAndMovie(user, movie).isPresent()) {
            return;
        }
        Review r = new Review();
        r.setUser(user);
        r.setMovie(movie);
        r.setContent(content);
        reviewRepo.save(r);
    }

    private static void ensureRating(RatingRepo ratingRepo, User user, Movie movie, int score) {
        if (ratingRepo.findByUserAndMovie(user, movie).isPresent()) {
            return;
        }
        Rating rating = new Rating();
        rating.setUser(user);
        rating.setMovie(movie);
        rating.setScore(score);
        ratingRepo.save(rating);
    }
}
