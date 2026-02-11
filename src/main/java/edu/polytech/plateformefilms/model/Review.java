package edu.polytech.plateformefilms.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @Column(length = 5000)
    private String content;
    private LocalDateTime createdAt;

    public Review() {
    }

    public Review(User user, Movie movie, String content) {
        this.user = user;
        this.movie = movie;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }
}
