package edu.polytech.plateformefilms.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

    private Integer score; // 1-5 or 1-10
    private LocalDateTime createdAt;

    public Rating() {
    }

    public Rating(User user, Movie movie, Integer score) {
        this.user = user;
        this.movie = movie;
        this.score = score;
        this.createdAt = LocalDateTime.now();
    }
}
