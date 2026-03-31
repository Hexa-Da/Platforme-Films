package edu.polytech.plateformefilms.service;

import edu.polytech.plateformefilms.model.Movie;
import edu.polytech.plateformefilms.repository.MovieRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    private final MovieRepo movieRepo;

    public MovieService(MovieRepo movieRepo) {
        this.movieRepo = movieRepo;
    }

    public List<Movie> findAll() {
        return movieRepo.findAll();
    }

    public Movie getById(Long id) {
        return movieRepo.findById(id).orElse(null);
    }

    public List<Movie> searchByTitle(String title) {
        return movieRepo.findByTitleContainingIgnoreCase(title);
    }

    public List<Movie> searchByGenre(String genre) {
        return movieRepo.findByGenreContainingIgnoreCase(genre);
    }

    public Movie createMovie(Movie movie) {
        if (movieRepo.existsByTitleIgnoreCaseAndDirectorIgnoreCaseAndReleaseYear(
                movie.getTitle(),
                movie.getDirector(),
                movie.getReleaseYear()
        )) {
            throw new IllegalArgumentException("Un film identique existe déjà");
        }
        return movieRepo.save(movie);
    }

    public Movie updateMovie(Long id, Movie updated) {
        Movie movie = getById(id);

        if (movie != null) {
            movie.setTitle(updated.getTitle());
            movie.setDirector(updated.getDirector());
            movie.setGenre(updated.getGenre());
            movie.setSynopsis(updated.getSynopsis());
            movie.setReleaseYear(updated.getReleaseYear());

            return movieRepo.save(movie);
        }

        return null;
    }

    public void deleteMovie(Long id) {
        if (movieRepo.existsById(id)) {
            movieRepo.deleteById(id);
        }
    }
}
