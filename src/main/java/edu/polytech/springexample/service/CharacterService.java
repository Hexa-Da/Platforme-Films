package edu.polytech.springexample.service;

import edu.polytech.springexample.model.FictionalCharacter;
import edu.polytech.springexample.repository.CharacterRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CharacterService {

    @Autowired
    private CharacterRepo repository;

    public FictionalCharacter saveFictionalCharacter(FictionalCharacter character) {
        return repository.save(character);
    }

    public List<FictionalCharacter> saveFictionalCharacters(List<FictionalCharacter> characters) {
        return repository.saveAll(characters);
    }

    public List<FictionalCharacter> getFictionalCharacters() {
        return repository.findAll();
    }

    public FictionalCharacter getFictionalCharacterById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public FictionalCharacter getFictionalCharacterByName(String lastname) {
        return repository.findByLastname(lastname);
    }

    public String deleteFictionalCharacter(Long id) {
        repository.deleteById(id);
        return "character removed !! " + id;
    }

    public FictionalCharacter updateFictionalCharacter(FictionalCharacter character) {
        FictionalCharacter existingFictionalCharacter = repository.findById(character.getId()).orElse(null);
        existingFictionalCharacter.setFirstname(character.getFirstname());
        existingFictionalCharacter.setLastname(character.getLastname());
        return repository.save(existingFictionalCharacter);
    }
    
}
