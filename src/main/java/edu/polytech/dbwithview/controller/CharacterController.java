package edu.polytech.dbwithview.controller;

import edu.polytech.dbwithview.model.FictionalCharacter;
import edu.polytech.dbwithview.service.CharacterService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CharacterController {

    @Autowired
    private CharacterService service;

    @PostMapping("/addCharacter")
    public FictionalCharacter addCharacter(@RequestBody FictionalCharacter character) {
        return service.saveFictionalCharacter(character);
    }

    @PostMapping("/addCharacters")
    public List<FictionalCharacter> addCharacters(@RequestBody List<FictionalCharacter> characters) {
        return service.saveFictionalCharacters(characters);
    }

    @GetMapping("/characters")
    public List<FictionalCharacter> findAllCharacters() {
        return service.getFictionalCharacters();
    }

    @GetMapping("/characterById/{id}")
    public FictionalCharacter findCharacterById(@PathVariable Long id) {
        return service.getFictionalCharacterById(id);
    }

    @GetMapping("/character/{name}")
    public FictionalCharacter findCharacterByName(@PathVariable String name) {
        return service.getFictionalCharacterByName(name);
    }

    @PutMapping("/update")
    public FictionalCharacter updateCharacter(@RequestBody FictionalCharacter character) {
        return service.updateFictionalCharacter(character);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteCharacter(@PathVariable Long id) {
        return service.deleteFictionalCharacter(id);
    }
}
     