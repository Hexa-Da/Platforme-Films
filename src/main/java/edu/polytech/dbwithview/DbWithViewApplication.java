package edu.polytech.dbwithview;

import edu.polytech.dbwithview.repository.CharacterRepo;
import edu.polytech.dbwithview.model.FictionalCharacter;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DbWithViewApplication {

    public static void main(String[] args) {
        SpringApplication.run(DbWithViewApplication.class, args);
    }

    @Bean
    CommandLineRunner dataLoader(CharacterRepo repository) {
        return args -> {
            repository.save(new FictionalCharacter(null, "Bilbo", "Baggins"));
            repository.save(new FictionalCharacter(null, "Hermione", "Granger"));
            repository.save(new FictionalCharacter(null, "Leia", "Organa"));
        };
    }
}
