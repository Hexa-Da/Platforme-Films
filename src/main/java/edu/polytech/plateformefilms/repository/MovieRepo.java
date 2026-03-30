package edu.polytech.plateformefilms.repository;

import edu.polytech.plateformefilms.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepo extends JpaRepository<Movie, Long> {
    List<Movie> findByTitleContainingIgnoreCase(String title);
    List<Movie> findByGenre(String genre);

    List<Movie> findByGenreContainingIgnoreCase(String genre);
    List<Movie> findByReleaseYear(Integer year);
}
