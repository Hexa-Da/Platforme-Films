package edu.polytech.plateformefilms.service;

import edu.polytech.plateformefilms.model.Movie;
import edu.polytech.plateformefilms.model.Rating;
import edu.polytech.plateformefilms.repository.MovieRepo;
import edu.polytech.plateformefilms.repository.RatingRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // Permet d'utiliser les @Mock
class RatingServiceTest {

    @Mock
    private RatingRepo ratingRepo;

    @Mock
    private MovieRepo movieRepo;

    @InjectMocks
    private RatingService ratingService; // Injecte les mocks ci-dessus dedans

    @Test
    void testGetAverageRating_ShouldReturnCorrectAverage() {
        // GIVEN (On prépare les données)
        Long movieId = 1L;
        Movie movie = new Movie();
        movie.setId(movieId);

        Rating r1 = new Rating(); r1.setScore(5);
        Rating r2 = new Rating(); r2.setScore(3);
        List<Rating> ratings = Arrays.asList(r1, r2);

        // On simule le comportement des repos
        when(movieRepo.findById(movieId)).thenReturn(Optional.of(movie));
        when(ratingRepo.findByMovie(movie)).thenReturn(ratings);

        // WHEN (On appelle la méthode à tester)
        Double average = ratingService.getAverageRating(movieId);

        // THEN (On vérifie le résultat)
        // (5 + 3) / 2 = 4.0
        assertEquals(4.0, average, "La moyenne devrait être de 4.0");
    }
}