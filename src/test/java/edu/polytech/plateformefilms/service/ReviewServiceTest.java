package edu.polytech.plateformefilms.service;

import edu.polytech.plateformefilms.model.Movie;
import edu.polytech.plateformefilms.model.Review;
import edu.polytech.plateformefilms.model.User;
import edu.polytech.plateformefilms.repository.MovieRepo;
import edu.polytech.plateformefilms.repository.ReviewRepo;
import edu.polytech.plateformefilms.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepo reviewRepo;
    @Mock
    private MovieRepo movieRepo;
    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private ReviewService reviewService;

    private User mockUser;
    private Movie mockMovie;
    private Review mockReview;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("alice");

        mockMovie = new Movie();
        mockMovie.setId(10L);
        mockMovie.setTitle("Inception");

        mockReview = new Review();
        mockReview.setId(100L);
        mockReview.setContent("Super film !");
        mockReview.setUser(mockUser);
        mockReview.setMovie(mockMovie);
    }

    @Test
    void getReviewsByMovie_ShouldReturnList() {
        // Arrange : On dit au mockMovie de se réveiller quand on cherche l'ID 10
        when(movieRepo.findById(10L)).thenReturn(Optional.of(mockMovie));
        // Et on dit au reviewRepo de renvoyer la liste quand on lui passe CET objet Movie
        when(reviewRepo.findByMovie(mockMovie)).thenReturn(List.of(mockReview));

        // Act
        List<Review> result = reviewService.getReviewsByMovie(10L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(movieRepo, times(1)).findById(10L);
        verify(reviewRepo, times(1)).findByMovie(mockMovie);
    }

    @Test
    void getReviewsByUser_ShouldReturnList() {
        // Arrange
        when(userRepo.findById(1L)).thenReturn(Optional.of(mockUser));
        when(reviewRepo.findByUser(mockUser)).thenReturn(List.of(mockReview));

        // Act
        List<Review> result = reviewService.getReviewsByUser(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepo, times(1)).findById(1L);
        verify(reviewRepo, times(1)).findByUser(mockUser);
    }

    @Test
    void createReview_ShouldSaveAndReturnReview() {
        // Arrange
        when(movieRepo.findById(10L)).thenReturn(Optional.of(mockMovie));
        when(userRepo.findById(1L)).thenReturn(Optional.of(mockUser));
        when(reviewRepo.save(any(Review.class))).thenReturn(mockReview);

        // Act
        Review result = reviewService.createReview(10L, 1L, "Super film !");

        // Assert
        assertNotNull(result);
        assertEquals("Super film !", result.getContent());
        assertEquals(mockUser, result.getUser());
    }

    @Test
    void deleteReview_WhenUserIsAuthor_ShouldDelete() {
        // Arrange
        when(reviewRepo.findById(100L)).thenReturn(Optional.of(mockReview));

        // Act
        reviewService.deleteReview(100L, 1L);

        // Assert
        verify(reviewRepo, times(1)).delete(mockReview);
    }

    @Test
    void deleteReview_WhenUserIsNotAuthor_ShouldThrowException() {
        // Arrange
        when(reviewRepo.findById(100L)).thenReturn(Optional.of(mockReview));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reviewService.deleteReview(100L, 2L); // User 2 essaie de supprimer la review de User 1
        });

        // On vérifie que la suppression N'A JAMAIS eu lieu
        verify(reviewRepo, never()).delete(any());
    }
}