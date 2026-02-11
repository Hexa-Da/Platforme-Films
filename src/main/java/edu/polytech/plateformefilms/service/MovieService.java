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
}
