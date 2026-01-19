package edu.polytech.dbwithview.repository;

import edu.polytech.dbwithview.model.FictionalCharacter;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CharacterRepo extends JpaRepository<FictionalCharacter, Long> {
    FictionalCharacter findByLastname(String lastname);
}
