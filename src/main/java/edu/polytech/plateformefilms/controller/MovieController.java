package edu.polytech.plateformefilms.controller;

import edu.polytech.plateformefilms.dto.MovieRequest;
import edu.polytech.plateformefilms.model.Movie;
import edu.polytech.plateformefilms.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
@Tag(name = "Movies", description = "Gestion des films")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @Operation(summary = "Récupérer un film par son ID", description = "Retourne les détails d'un film. Renvoie 404 si l'ID n'existe pas.")
    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        Movie movie = movieService.getById(id);
        return movie != null ? ResponseEntity.ok(movie) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Lister ou rechercher des films", description = "Récupère tous les films. Possibilité de filtrer par titre ou par genre.")
    @GetMapping
    public ResponseEntity<List<Movie>> getAllMovies(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String genre) {

        if (title != null && !title.isBlank()) {
            return ResponseEntity.ok(movieService.searchByTitle(title.trim()));
        }
        if (genre != null && !genre.isBlank()) {
            return ResponseEntity.ok(movieService.searchByGenre(genre.trim()));
        }
        return ResponseEntity.ok(movieService.findAll());
    }


    @Operation(summary = "Ajouter un nouveau film", description = "Crée un film dans la base de données")
    @PostMapping
    public ResponseEntity<Movie> createMovie(@RequestBody MovieRequest request) {
        Movie movie = new Movie();
        movie.setTitle(request.title());
        movie.setDirector(request.director());
        movie.setGenre(request.genre());
        movie.setSynopsis(request.synopsis());
        movie.setReleaseYear(request.releaseYear());

        Movie created;
        try {
            created = movieService.createMovie(movie);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }

        return ResponseEntity.status(201).body(created); // 201 = ressource créée
    }


    @Operation(summary = "Modifier un film existant", description = "Met à jour les informations d'un film et renvoie le film modifié. Renvoie 404 si l'ID n'existe pas.")
    @PutMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id, @RequestBody MovieRequest request) {
        Movie updated = new Movie();
        updated.setTitle(request.title());
        updated.setDirector(request.director());
        updated.setGenre(request.genre());
        updated.setSynopsis(request.synopsis());
        updated.setReleaseYear(request.releaseYear());

        Movie newMovie = movieService.updateMovie(id, updated);

        return newMovie != null ? ResponseEntity.ok(newMovie) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Supprimer un film", description = "Supprime un film de la base de données via son ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build(); //noContent => renvoie 204 (traité mais rien à ne retouner)
    }
}
