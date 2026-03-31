package edu.polytech.plateformefilms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.polytech.plateformefilms.dto.RatingRequest;
import edu.polytech.plateformefilms.model.Rating;
import edu.polytech.plateformefilms.model.User;
import edu.polytech.plateformefilms.repository.MovieRepo;
import edu.polytech.plateformefilms.security.JwtAuthenticationFilter;
import edu.polytech.plateformefilms.security.OAuth2AuthenticationSuccessHandler;
import edu.polytech.plateformefilms.service.RatingService;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RatingController.class)
@AutoConfigureMockMvc(addFilters = false) // Désactive la sécurité pour éviter le 302 Redirect
class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RatingService ratingService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private MovieRepo movieRepo;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    private Authentication mockAuth;

    @BeforeEach
    void setup() {
        // On prépare un faux objet Authentication réutilisable qui renvoie "bob"
        mockAuth = mock(Authentication.class);
        when(mockAuth.getName()).thenReturn("bob");
    }

    // --- TEST 1 : MOYENNE ---
    @Test
    void getAverageRating_ShouldReturnDouble() throws Exception {
        when(ratingService.getAverageRating(1L)).thenReturn(4.2);

        mockMvc.perform(get("/api/v1/movies/1/ratings/average"))
                .andExpect(status().isOk())
                .andExpect(content().string("4.2"));
    }

    // --- TEST 2 : LISTE DES NOTES ---
    @Test
    void getRatingsByMovie_ShouldReturnList() throws Exception {
        Rating r = new Rating();
        r.setScore(5);
        User u = new User();
        u.setUsername("bob");
        r.setUser(u);

        when(ratingService.getRatingsByMovie(1L)).thenReturn(List.of(r));

        mockMvc.perform(get("/api/v1/movies/1/ratings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].score").value(5))
                .andExpect(jsonPath("$[0].username").value("bob"));
    }

    // --- TEST 3 : CRÉATION (AVEC AUTH) ---
    @Test
    void rateMovie_WhenAuthenticated_ShouldReturn201() throws Exception {
        RatingRequest request = new RatingRequest(4);
        User mockUser = new User();
        mockUser.setId(2L);
        mockUser.setUsername("bob");

        Rating mockRating = new Rating();
        mockRating.setId(50L);
        mockRating.setScore(4);
        mockRating.setUser(mockUser);

        when(userService.findByUsername("bob")).thenReturn(mockUser);
        when(ratingService.rateMovie(eq(1L), eq(2L), eq(4))).thenReturn(mockRating);

        mockMvc.perform(post("/api/v1/movies/1/ratings")
                        .principal(mockAuth) // Injection manuelle du mock d'auth
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.score").value(4))
                .andExpect(jsonPath("$.username").value("bob"));
    }

    @Test
    void rateMovie_WhenUserNotFound_ShouldReturn401() throws Exception {
        RatingRequest request = new RatingRequest(4);
        when(userService.findByUsername("bob")).thenReturn(null);

        mockMvc.perform(post("/api/v1/movies/1/ratings")
                        .principal(mockAuth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rateMovie_WhenMovieNotFound_ShouldReturn404() throws Exception {
        RatingRequest request = new RatingRequest(4);
        User mockUser = new User();
        mockUser.setId(2L);
        mockUser.setUsername("bob");
        when(userService.findByUsername("bob")).thenReturn(mockUser);
        when(ratingService.rateMovie(eq(1L), eq(2L), eq(4)))
                .thenThrow(new RuntimeException("Film non trouvé"));

        mockMvc.perform(post("/api/v1/movies/1/ratings")
                        .principal(mockAuth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void rateMovie_WhenScoreOutOfRange_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/api/v1/movies/1/ratings")
                        .principal(mockAuth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"score\": 6}"))
                .andExpect(status().isBadRequest());
    }
}