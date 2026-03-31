package edu.polytech.plateformefilms.repository;

import edu.polytech.plateformefilms.model.Movie;
import edu.polytech.plateformefilms.model.Review;
import edu.polytech.plateformefilms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepo extends JpaRepository<Review, Long> {
    void deleteByMovie_Id(Long movieId);

    List<Review> findByMovie(Movie movie);
    List<Review> findByUser(User user);
    Optional<Review> findByUserAndMovie(User user, Movie movie);
}
