package edu.polytech.springexample;

import edu.polytech.springexample.repository.CharacterRepo;
import edu.polytech.springexample.model.FictionalCharacter;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringExampleApplication.class, args);
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
