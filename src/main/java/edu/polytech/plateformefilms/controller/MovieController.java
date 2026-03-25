package edu.polytech.plateformefilms.controller;

import edu.polytech.plateformefilms.dto.MovieRequest;
import edu.polytech.plateformefilms.model.Movie;
import edu.polytech.plateformefilms.service.MovieService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
@Tag(name = "Movies", description = "Gestion des films")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        Movie movie = movieService.getById(id);
        return movie != null ? ResponseEntity.ok(movie) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<Movie>> getAllMovies(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String genre) {

        if (title != null) return ResponseEntity.ok(movieService.searchByTitle(title));
        if (genre != null) return ResponseEntity.ok(movieService.searchByGenre(genre));
        return ResponseEntity.ok(movieService.findAll());
    }


    @PostMapping
    public ResponseEntity<Movie> createMovie(@RequestBody MovieRequest request) {
        Movie movie = new Movie();
        movie.setTitle(request.title());
        movie.setDirector(request.director());
        movie.setGenre(request.genre());
        movie.setSynopsis(request.synopsis());
        movie.setReleaseYear(request.releaseYear());

        Movie created = movieService.createMovie(movie);

        return ResponseEntity.status(201).body(created); // 201 = ressource créée
    }


    @PutMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id, @RequestBody MovieRequest request) {
        Movie updated = new Movie();
        updated.setTitle(request.title());
        updated.setDirector(request.director());
        updated.setGenre(request.genre());
        updated.setSynopsis(request.synopsis());
        updated.setReleaseYear(request.releaseYear());

        Movie newMovie = movieService.updateMovie(id, updated);

        return ResponseEntity.ok(newMovie); // 200 : requête traitée
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build(); //noContent => renvoie 204 (traité mais rien à ne retouner)
    }
}
