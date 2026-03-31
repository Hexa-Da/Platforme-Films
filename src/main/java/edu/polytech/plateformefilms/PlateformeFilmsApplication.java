package edu.polytech.plateformefilms;

import edu.polytech.plateformefilms.model.Movie;
import edu.polytech.plateformefilms.repository.MovieRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;

@SpringBootApplication
public class PlateformeFilmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlateformeFilmsApplication.class, args);
    }

    @Bean
    @Order(1)
    CommandLineRunner dataLoader(
            MovieRepo movieRepo,
            @Value("${app.seed.demo-movies.enabled:true}") boolean demoMoviesSeedEnabled
    ) {
        return args -> {
            if (!demoMoviesSeedEnabled) {
                return;
            }
            seedMovieIfMissing(
                    movieRepo,
                    "Inception",
                    "Christopher Nolan",
                    2010,
                    "Sci-Fi",
                    "A thief who steals corporate secrets through dream-sharing technology."
            );
            seedMovieIfMissing(
                    movieRepo,
                    "Parasite",
                    "Bong Joon-ho",
                    2019,
                    "Thriller",
                    "A poor family schemes to become employed by a wealthy family."
            );
            seedMovieIfMissing(
                    movieRepo,
                    "The Dark Knight",
                    "Christopher Nolan",
                    2008,
                    "Action",
                    "Batman must accept one of the greatest psychological tests."
            );
            seedMovieIfMissing(
                    movieRepo,
                    "Interstellar",
                    "Christopher Nolan",
                    2014,
                    "Sci-Fi",
                    "A team of explorers travel through a wormhole in space."
            );
            seedMovieIfMissing(
                    movieRepo,
                    "Dune",
                    "Denis Villeneuve",
                    2021,
                    "Sci-Fi",
                    "A noble family becomes embroiled in a war for control of the universe's most valuable asset."
            );
        };
    }

    private static void seedMovieIfMissing(
            MovieRepo movieRepo,
            String title,
            String director,
            int releaseYear,
            String genre,
            String synopsis
    ) {
        if (!movieRepo.existsByTitleIgnoreCaseAndDirectorIgnoreCaseAndReleaseYear(title, director, releaseYear)) {
            movieRepo.save(new Movie(title, director, releaseYear, genre, synopsis));
        }
    }
}
