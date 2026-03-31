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
    // TESTS POUR RATE MOVIE (Création vs Mise à jour)
    // --------------------------------------------------------

    @Test
    void rateMovie_WhenNewRating_ShouldCreateAndSave() {
        // GIVEN : L'utilisateur n'a pas encore noté le film
        when(movieRepo.findById(1L)).thenReturn(Optional.of(mockMovie));
        when(userRepo.findById(10L)).thenReturn(Optional.of(mockUser));

        // On simule que le repo ne trouve pas de note existante
        // Note : Si ton findByUserAndMovie renvoie Rating (et pas Optional<Rating>), remplace Optional.empty() par null
        when(ratingRepo.findByUserAndMovie(mockUser, mockMovie)).thenReturn(Optional.empty());

        // On simule la sauvegarde en renvoyant l'objet passé en paramètre
        when(ratingRepo.save(any(Rating.class))).thenAnswer(i -> i.getArgument(0));

        // WHEN
        Rating result = ratingService.rateMovie(1L, 10L, 5);

        // THEN
        assertNotNull(result);
        assertEquals(5, result.getScore());
        verify(ratingRepo, times(1)).save(any(Rating.class)); // On a bien sauvegardé une NOUVELLE note
    }

    @Test
    void rateMovie_WhenExistingRating_ShouldUpdateAndSave() {
        // GIVEN : L'utilisateur a DÉJÀ noté le film (note de 3 préparée dans le setUp)
        when(movieRepo.findById(1L)).thenReturn(Optional.of(mockMovie));
        when(userRepo.findById(10L)).thenReturn(Optional.of(mockUser));

        // Le repo trouve la note existante
        when(ratingRepo.findByUserAndMovie(mockUser, mockMovie)).thenReturn(Optional.of(mockRating));
        when(ratingRepo.save(any(Rating.class))).thenReturn(mockRating);

        // WHEN : L'utilisateur change d'avis et met 5
        Rating result = ratingService.rateMovie(1L, 10L, 5);

        // THEN
        assertEquals(5, result.getScore(), "Le score devrait être mis à jour à 5");
        verify(ratingRepo, times(1)).save(mockRating); // On a sauvegardé par-dessus la note EXISTANTE
    }
}