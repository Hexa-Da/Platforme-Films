package edu.polytech.plateformefilms;

import edu.polytech.plateformefilms.model.Movie;
import edu.polytech.plateformefilms.repository.MovieRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PlateformeFilmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlateformeFilmsApplication.class, args);
    }

    @Bean
    CommandLineRunner dataLoader(MovieRepo movieRepo) {
        return args -> {
            movieRepo.save(new Movie("Inception", "Christopher Nolan", 2010, "Sci-Fi", "A thief who steals corporate secrets through dream-sharing technology."));
            movieRepo.save(new Movie("Parasite", "Bong Joon-ho", 2019, "Thriller", "A poor family schemes to become employed by a wealthy family."));
            movieRepo.save(new Movie("The Dark Knight", "Christopher Nolan", 2008, "Action", "Batman must accept one of the greatest psychological tests."));
            movieRepo.save(new Movie("Interstellar", "Christopher Nolan", 2014, "Sci-Fi", "A team of explorers travel through a wormhole in space."));
            movieRepo.save(new Movie("Dune", "Denis Villeneuve", 2021, "Sci-Fi", "A noble family becomes embroiled in a war for control of the universe's most valuable asset."));
        };
    }
}
