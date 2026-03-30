package edu.polytech.plateformefilms.service;

import edu.polytech.plateformefilms.model.Movie;
import edu.polytech.plateformefilms.repository.MovieRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepo movieRepo;

    @InjectMocks
    private MovieService movieService;

    @Test
    void searchByTitle_delegatesToRepo() {
        List<Movie> expected = List.of(new Movie("A", "d", 2020, "g", "s"));
        when(movieRepo.findByTitleContainingIgnoreCase("foo")).thenReturn(expected);

        assertEquals(expected, movieService.searchByTitle("foo"));
        verify(movieRepo).findByTitleContainingIgnoreCase("foo");
    }

    @Test
    void searchByGenre_delegatesToRepo() {
        List<Movie> expected = List.of(new Movie("A", "d", 2020, "g", "s"));
        when(movieRepo.findByGenre("SciFi")).thenReturn(expected);

        assertEquals(expected, movieService.searchByGenre("SciFi"));
        verify(movieRepo).findByGenre("SciFi");
    }

    @Test
    void createMovie_savesAndReturns() {
        Movie input = new Movie("T", "D", 2021, "G", "Syn");
        Movie saved = new Movie("T", "D", 2021, "G", "Syn");
        saved.setId(1L);
        when(movieRepo.save(input)).thenReturn(saved);

        assertEquals(saved, movieService.createMovie(input));
        verify(movieRepo).save(input);
    }

    @Test
    void updateMovie_whenExists_updatesFieldsAndSaves() {
        Long id = 1L;
        Movie existing = new Movie("Old", "od", 2000, "og", "os");
        existing.setId(id);
        Movie updated = new Movie("New", "nd", 2021, "ng", "ns");

        when(movieRepo.findById(id)).thenReturn(Optional.of(existing));
        when(movieRepo.save(existing)).thenAnswer(inv -> inv.getArgument(0));

        Movie result = movieService.updateMovie(id, updated);

        assertEquals("New", result.getTitle());
        assertEquals("nd", result.getDirector());
        assertEquals(2021, result.getReleaseYear());
        assertEquals("ng", result.getGenre());
        assertEquals("ns", result.getSynopsis());
        verify(movieRepo).save(existing);
    }

    @Test
    void updateMovie_whenMissing_returnsNullAndDoesNotSave() {
        when(movieRepo.findById(99L)).thenReturn(Optional.empty());

        assertNull(movieService.updateMovie(99L, new Movie()));

        verify(movieRepo, never()).save(any());
    }

    @Test
    void deleteMovie_whenExists_deletes() {
        when(movieRepo.existsById(1L)).thenReturn(true);

        movieService.deleteMovie(1L);

        verify(movieRepo).deleteById(1L);
    }

    @Test
    void deleteMovie_whenMissing_doesNotDelete() {
        when(movieRepo.existsById(2L)).thenReturn(false);

        movieService.deleteMovie(2L);

        verify(movieRepo, never()).deleteById(any());
    }
}
