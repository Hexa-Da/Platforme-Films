package edu.polytech.plateformefilms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.polytech.plateformefilms.dto.MovieRequest;
import edu.polytech.plateformefilms.model.Movie;
import edu.polytech.plateformefilms.repository.MovieRepo;
import edu.polytech.plateformefilms.security.JwtAuthenticationFilter;
import edu.polytech.plateformefilms.security.OAuth2AuthenticationSuccessHandler;
import edu.polytech.plateformefilms.service.MovieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieController.class)
@AutoConfigureMockMvc(addFilters = false) // Désactive la sécurité pour simplifier les tests CRUD de films
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean // Remplace @MockBean
    private MovieService movieService;

    @MockitoBean
    private MovieRepo movieRepo;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Test
    void getAllMovies_returnsList() throws Exception {
        Movie m = new Movie("T", "D", 2020, "G", "S");
        m.setId(1L);
        when(movieService.findAll()).thenReturn(List.of(m));

        mockMvc.perform(get("/api/v1/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("T"));

        verify(movieService).findAll();
    }

    @Test
    void getAllMovies_withTitle_searchesByTitle() throws Exception {
        when(movieService.searchByTitle("foo")).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/movies").param("title", "foo"))
                .andExpect(status().isOk());

        verify(movieService).searchByTitle("foo");
    }

    @Test
    void getAllMovies_withGenre_searchesByGenre() throws Exception {
        when(movieService.searchByGenre("Drama")).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/movies").param("genre", "Drama"))
                .andExpect(status().isOk());

        verify(movieService).searchByGenre("Drama");
    }

    @Test
    void getMovieById_found_returns200() throws Exception {
        Movie m = new Movie("T", "D", 2020, "G", "S");
        m.setId(1L);
        when(movieService.getById(1L)).thenReturn(m);

        mockMvc.perform(get("/api/v1/movies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("T"));
    }

    @Test
    void getMovieById_notFound_returns404() throws Exception {
        when(movieService.getById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/v1/movies/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createMovie_returns201() throws Exception {
        Movie created = new Movie("T", "D", 2021, "G", "Syn");
        created.setId(5L);
        when(movieService.createMovie(any(Movie.class))).thenReturn(created);

        MovieRequest req = new MovieRequest("T", "D", 2021, "G", "Syn");

        mockMvc.perform(post("/api/v1/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.title").value("T"));
    }

    @Test
    void updateMovie_found_returns200() throws Exception {
        Movie updated = new Movie("N", "D", 2022, "G", "S");
        updated.setId(1L);
        when(movieService.updateMovie(eq(1L), any(Movie.class))).thenReturn(updated);

        MovieRequest req = new MovieRequest("N", "D", 2022, "G", "S");

        mockMvc.perform(put("/api/v1/movies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("N"));
    }

    @Test
    void updateMovie_notFound_returns404() throws Exception {
        when(movieService.updateMovie(eq(2L), any(Movie.class))).thenReturn(null);

        MovieRequest req = new MovieRequest("N", "D", 2022, "G", "S");

        mockMvc.perform(put("/api/v1/movies/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteMovie_whenExists_returns204() throws Exception {
        when(movieService.deleteMovie(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/movies/1"))
                .andExpect(status().isNoContent());

        verify(movieService).deleteMovie(1L);
    }

    @Test
    void deleteMovie_whenMissing_returns404() throws Exception {
        when(movieService.deleteMovie(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/movies/99"))
                .andExpect(status().isNotFound());
    }
}