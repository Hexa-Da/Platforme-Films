package edu.polytech.plateformefilms.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Data
@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "uk_movie_title_director_release_year",
                columnNames = {"title", "director", "releaseYear"}
        )
)
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
