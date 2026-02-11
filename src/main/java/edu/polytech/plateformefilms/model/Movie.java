package edu.polytech.plateformefilms.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String director;
    private Integer releaseYear;
    private String genre;
    private String synopsis;

    public Movie() {
    }

    public Movie(String title, String director, Integer releaseYear, String genre, String synopsis) {
        this.title = title;
        this.director = director;
        this.releaseYear = releaseYear;
        this.genre = genre;
        this.synopsis = synopsis;
    }
}
