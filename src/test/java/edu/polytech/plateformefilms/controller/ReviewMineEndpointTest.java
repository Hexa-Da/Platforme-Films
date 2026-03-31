package edu.polytech.plateformefilms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.polytech.plateformefilms.config.GlobalExceptionHandler;
import edu.polytech.plateformefilms.dto.ReviewRequest;
import edu.polytech.plateformefilms.exception.NotFoundException;
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
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewMineEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReviewService reviewService;

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
    void updateMyReview_ShouldReturn200() throws Exception {
        ReviewRequest request = new ReviewRequest("Modifié via /mine");
        Review updated = new Review();
        updated.setId(50L);
        updated.setContent("Modifié via /mine");
        updated.setUser(mockUser);

        when(reviewService.updateMyReview(1L, 2L, "Modifié via /mine")).thenReturn(updated);

        mockMvc.perform(put("/api/v1/movies/1/reviews/mine")
                        .principal(mockAuth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Modifié via /mine"));
    }

    @Test
    void updateMyReview_WhenNoExistingReview_ShouldReturn404() throws Exception {
        ReviewRequest request = new ReviewRequest("Texte");
        when(reviewService.updateMyReview(1L, 2L, "Texte"))
                .thenThrow(new NotFoundException("Critique introuvable"));

        mockMvc.perform(put("/api/v1/movies/1/reviews/mine")
                        .principal(mockAuth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateMyReview_WhenUserNotFound_ShouldReturn401() throws Exception {
        ReviewRequest request = new ReviewRequest("Texte");
        when(userService.findByUsername("bob")).thenReturn(null);

        mockMvc.perform(put("/api/v1/movies/1/reviews/mine")
                        .principal(mockAuth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
