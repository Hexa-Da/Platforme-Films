package edu.polytech.plateformefilms.service;

import edu.polytech.plateformefilms.model.Movie;
import edu.polytech.plateformefilms.model.Rating;
import edu.polytech.plateformefilms.model.User;
import edu.polytech.plateformefilms.repository.MovieRepo;
import edu.polytech.plateformefilms.repository.RatingRepo;
import edu.polytech.plateformefilms.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepo ratingRepo;

    @Mock
    private MovieRepo movieRepo;

    @Mock
    private UserRepo userRepo; // Ajouté pour tester rateMovie()

    @InjectMocks
    private RatingService ratingService;

    // Objets réutilisables pour les tests
    private Movie mockMovie;
    private User mockUser;
    private Rating mockRating;

    @BeforeEach
    void setUp() {
        mockMovie = new Movie();
        mockMovie.setId(1L);

        mockUser = new User();
        mockUser.setId(10L);

        mockRating = new Rating();
        mockRating.setId(100L);
        mockRating.setScore(3);
        mockRating.setMovie(mockMovie);
        mockRating.setUser(mockUser);
    }

    // --------------------------------------------------------
    // TESTS POUR GET AVERAGE RATING
    // --------------------------------------------------------

    @Test
    void testGetAverageRating_ShouldReturnCorrectAverage() {
        // GIVEN
        Rating r1 = new Rating(); r1.setScore(5);
        Rating r2 = new Rating(); r2.setScore(3);

        when(movieRepo.findById(1L)).thenReturn(Optional.of(mockMovie));
        when(ratingRepo.findByMovie(mockMovie)).thenReturn(Arrays.asList(r1, r2));

        // WHEN
        Double average = ratingService.getAverageRating(1L);

        // THEN
        assertEquals(4.0, average, "La moyenne devrait être de 4.0");
    }

    @Test
    void getAverageRating_WhenNoRatings_ShouldReturnZero() {
        when(movieRepo.findById(1L)).thenReturn(Optional.of(mockMovie));
        when(ratingRepo.findByMovie(mockMovie)).thenReturn(Collections.emptyList()); // Aucune note

        Double average = ratingService.getAverageRating(1L);

        assertEquals(0.0, average, "Devrait retourner 0.0 si le film n'a aucune note");
    }

    // --------------------------------------------------------
    // TESTS POUR GET RATINGS BY MOVIE
    // --------------------------------------------------------

    @Test
    void getRatingsByMovie_ShouldReturnList() {
        when(movieRepo.findById(1L)).thenReturn(Optional.of(mockMovie));
        when(ratingRepo.findByMovie(mockMovie)).thenReturn(List.of(mockRating));

        List<Rating> result = ratingService.getRatingsByMovie(1L);

        assertEquals(1, result.size());
        assertEquals(3, result.get(0).getScore());
    }

    // --------------------------------------------------------
    // TESTS POUR CREATE RATING / UPDATE RATING
    // --------------------------------------------------------

    @Test
    void createRating_WhenNewRating_ShouldCreateAndSave() {
        when(movieRepo.findById(1L)).thenReturn(Optional.of(mockMovie));
        when(userRepo.findById(10L)).thenReturn(Optional.of(mockUser));
        when(ratingRepo.findByUserAndMovie(mockUser, mockMovie)).thenReturn(Optional.empty());
        when(ratingRepo.save(any(Rating.class))).thenAnswer(i -> i.getArgument(0));

        Rating result = ratingService.createRating(1L, 10L, 5);

        assertNotNull(result);
        assertEquals(5, result.getScore());
        verify(ratingRepo, times(1)).save(any(Rating.class));
    }

    @Test
    void createRating_WhenExistingRating_ShouldThrowException() {
        when(movieRepo.findById(1L)).thenReturn(Optional.of(mockMovie));
        when(userRepo.findById(10L)).thenReturn(Optional.of(mockUser));
        when(ratingRepo.findByUserAndMovie(mockUser, mockMovie)).thenReturn(Optional.of(mockRating));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                ratingService.createRating(1L, 10L, 5));

        assertTrue(exception.getMessage().contains("déjà"));
        verify(ratingRepo, never()).save(any());
    }

    @Test
    void updateRating_WhenAuthor_ShouldUpdateAndSave() {
        when(ratingRepo.findById(100L)).thenReturn(Optional.of(mockRating));
        when(ratingRepo.save(mockRating)).thenReturn(mockRating);

        Rating updated = ratingService.updateRating(1L, 100L, 10L, 5);

        assertEquals(5, updated.getScore());
        verify(ratingRepo, times(1)).save(mockRating);
    }

    @Test
    void updateRating_WhenNotAuthor_ShouldThrowException() {
        when(ratingRepo.findById(100L)).thenReturn(Optional.of(mockRating));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                ratingService.updateRating(1L, 100L, 99L, 5));

        assertTrue(exception.getMessage().contains("Interdit"));
        verify(ratingRepo, never()).save(any());
    }

    @Test
    void updateRating_WhenMovieMismatch_ShouldThrowException() {
        when(ratingRepo.findById(100L)).thenReturn(Optional.of(mockRating));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                ratingService.updateRating(99L, 100L, 10L, 5));

        assertTrue(exception.getMessage().contains("introuvable"));
        verify(ratingRepo, never()).save(any());
    }
}