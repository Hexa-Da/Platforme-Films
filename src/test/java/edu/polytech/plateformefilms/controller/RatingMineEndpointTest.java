package edu.polytech.plateformefilms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.polytech.plateformefilms.config.GlobalExceptionHandler;
import edu.polytech.plateformefilms.dto.RatingRequest;
import edu.polytech.plateformefilms.exception.NotFoundException;
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
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RatingController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class RatingMineEndpointTest {

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
    private User mockUser;

    @BeforeEach
    void setup() {
        mockAuth = mock(Authentication.class);
        when(mockAuth.getName()).thenReturn("bob");
        mockUser = new User();
        mockUser.setId(2L);
        mockUser.setUsername("bob");
        when(userService.findByUsername("bob")).thenReturn(mockUser);
    }

    @Test
    void updateMyRating_ShouldReturn200() throws Exception {
        RatingRequest request = new RatingRequest(5);
        Rating updated = new Rating();
        updated.setId(50L);
        updated.setScore(5);
        updated.setUser(mockUser);

        when(ratingService.updateMyRating(1L, 2L, 5)).thenReturn(updated);

        mockMvc.perform(put("/api/v1/movies/1/ratings/mine")
                        .principal(mockAuth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(5));
    }

    @Test
    void updateMyRating_WhenNoExistingRating_ShouldReturn404() throws Exception {
        RatingRequest request = new RatingRequest(3);
        when(ratingService.updateMyRating(1L, 2L, 3))
                .thenThrow(new NotFoundException("Note introuvable"));

        mockMvc.perform(put("/api/v1/movies/1/ratings/mine")
                        .principal(mockAuth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateMyRating_WhenUserNotFound_ShouldReturn401() throws Exception {
        RatingRequest request = new RatingRequest(3);
        when(userService.findByUsername("bob")).thenReturn(null);

        mockMvc.perform(put("/api/v1/movies/1/ratings/mine")
                        .principal(mockAuth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
