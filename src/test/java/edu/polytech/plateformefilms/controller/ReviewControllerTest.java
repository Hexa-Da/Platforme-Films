package edu.polytech.plateformefilms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.polytech.plateformefilms.dto.ReviewRequest;
import edu.polytech.plateformefilms.model.Review;
import edu.polytech.plateformefilms.model.User;
import edu.polytech.plateformefilms.repository.MovieRepo;
import edu.polytech.plateformefilms.security.JwtAuthenticationFilter;
import edu.polytech.plateformefilms.security.OAuth2AuthenticationSuccessHandler;
import edu.polytech.plateformefilms.service.ReviewService;
import edu.polytech.plateformefilms.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false) // Désactive la sécurité pour éviter le 302 Redirect
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private UserService userService;

    // --- Mocks de "Nettoyage" pour le contexte Spring ---
    @MockitoBean
    private MovieRepo movieRepo;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    private Authentication mockAuth;

    @BeforeEach
    void setup() {
        // On prépare un faux objet Authentication qui renvoie "bob"
        mockAuth = mock(Authentication.class);
        when(mockAuth.getName()).thenReturn("bob");
    }

    @Test
    void getReviews_ShouldReturnList() throws Exception {
        // GIVEN
        Review r = new Review();
        r.setContent("Super film !");
        User u = new User();
        u.setUsername("bob");
        r.setUser(u);

        when(reviewService.getReviewsByMovie(1L)).thenReturn(List.of(r));

        // WHEN & THEN
        mockMvc.perform(get("/api/v1/movies/1/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Super film !"))
                .andExpect(jsonPath("$[0].username").value("bob"));
    }

    @Test
    void createReview_WhenAuthenticated_ShouldReturn201() throws Exception {
        // GIVEN
        ReviewRequest request = new ReviewRequest("J'ai adoré !");
        User mockUser = new User();
        mockUser.setId(2L);
        mockUser.setUsername("bob");

        Review mockReview = new Review();
        mockReview.setId(100L);
        mockReview.setContent("J'ai adoré !");
        mockReview.setUser(mockUser);

        when(userService.findByUsername("bob")).thenReturn(mockUser);
        when(reviewService.createReview(eq(1L), eq(2L), anyString())).thenReturn(mockReview);

        // WHEN & THEN
        mockMvc.perform(post("/api/v1/movies/1/reviews")
                        .principal(mockAuth) // Injection manuelle pour éviter le NullPointer
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("J'ai adoré !"))
                .andExpect(jsonPath("$.username").value("bob"));
    }

    @Test
    void deleteReview_AsAuthor_ShouldReturn204() throws Exception {
        // GIVEN
        User mockUser = new User();
        mockUser.setId(2L);
        when(userService.findByUsername("bob")).thenReturn(mockUser);
        Review review = new Review();
        review.setId(100L);
        review.setUser(mockUser);
        edu.polytech.plateformefilms.model.Movie movie = new edu.polytech.plateformefilms.model.Movie();
        movie.setId(1L);
        review.setMovie(movie);
        when(reviewService.getReviewById(100L)).thenReturn(review);

        // WHEN & THEN
        mockMvc.perform(delete("/api/v1/movies/1/reviews/100")
                        .principal(mockAuth))
                .andExpect(status().isNoContent());
    }

    @Test
    void createReview_WhenUserNotFound_ShouldReturn401() throws Exception {
        ReviewRequest request = new ReviewRequest("Texte");
        when(userService.findByUsername("bob")).thenReturn(null);

        mockMvc.perform(post("/api/v1/movies/1/reviews")
                        .principal(mockAuth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteReview_WhenNotAuthor_ShouldReturn403() throws Exception {
        User mockUser = new User();
        mockUser.setId(2L);
        when(userService.findByUsername("bob")).thenReturn(mockUser);
        Review review = new Review();
        review.setId(100L);
        review.setUser(mockUser);
        edu.polytech.plateformefilms.model.Movie movie = new edu.polytech.plateformefilms.model.Movie();
        movie.setId(1L);
        review.setMovie(movie);
        when(reviewService.getReviewById(100L)).thenReturn(review);
        doThrow(new RuntimeException("Interdit : Vous n'êtes pas l'auteur de cette critique !"))
                .when(reviewService).deleteReview(100L, 2L);

        mockMvc.perform(delete("/api/v1/movies/1/reviews/100")
                        .principal(mockAuth))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteReview_WhenMovieReviewMismatch_ShouldReturn404() throws Exception {
        User mockUser = new User();
        mockUser.setId(2L);
        when(userService.findByUsername("bob")).thenReturn(mockUser);
        Review review = new Review();
        review.setId(100L);
        review.setUser(mockUser);
        edu.polytech.plateformefilms.model.Movie movie = new edu.polytech.plateformefilms.model.Movie();
        movie.setId(99L);
        review.setMovie(movie);
        when(reviewService.getReviewById(100L)).thenReturn(review);

        mockMvc.perform(delete("/api/v1/movies/1/reviews/100")
                        .principal(mockAuth))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteReview_WhenReviewNotFound_ShouldReturn404() throws Exception {
        User mockUser = new User();
        mockUser.setId(2L);
        when(userService.findByUsername("bob")).thenReturn(mockUser);
        when(reviewService.getReviewById(100L)).thenThrow(new RuntimeException("Critique introuvable"));

        mockMvc.perform(delete("/api/v1/movies/1/reviews/100")
                        .principal(mockAuth))
                .andExpect(status().isNotFound());
    }
}