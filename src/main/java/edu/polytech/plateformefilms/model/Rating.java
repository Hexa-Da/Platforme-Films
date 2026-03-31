package edu.polytech.plateformefilms.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(uniqueConstraints = @UniqueConstraint(
        name = "uk_rating_user_movie",
        columnNames = {"user_id", "movie_id"}
))
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    private Integer score;
    private LocalDateTime createdAt;

    public Rating() {
    }

    public Rating(User user, Movie movie, Integer score) {
        this.user = user;
        this.movie = movie;
        this.score = score;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
