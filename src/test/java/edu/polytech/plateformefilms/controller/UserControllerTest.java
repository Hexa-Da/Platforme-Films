package edu.polytech.plateformefilms.controller;

import edu.polytech.plateformefilms.model.User;
import edu.polytech.plateformefilms.repository.MovieRepo;
import edu.polytech.plateformefilms.security.JwtAuthenticationFilter;
import edu.polytech.plateformefilms.security.OAuth2AuthenticationSuccessHandler;
import edu.polytech.plateformefilms.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private MovieRepo movieRepo;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Test
    @WithMockUser
    void getUserById_found_returns200() throws Exception {
        User u = new User();
        u.setId(1L);
        u.setUsername("alice");
        u.setEmail("a@b.c");
        u.setCreatedAt(LocalDateTime.of(2024, 1, 1, 12, 0));
        when(userService.getById(1L)).thenReturn(u);

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.email").value("a@b.c"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @WithMockUser
    void getUserById_notFound_returns404() throws Exception {
        when(userService.getById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/v1/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "bob")
    void getMyProfile_found_returns200() throws Exception {
        User u = new User();
        u.setId(2L);
        u.setUsername("bob");
        u.setEmail("bob@example.com");
        u.setCreatedAt(LocalDateTime.of(2024, 6, 15, 10, 30));
        when(userService.findByUsername("bob")).thenReturn(u);

        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("bob"))
                .andExpect(jsonPath("$.email").value("bob@example.com"));
    }

    @Test
    @WithMockUser(username = "orphan")
    void getMyProfile_userMissing_returns404() throws Exception {
        when(userService.findByUsername("orphan")).thenReturn(null);

        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isNotFound());
    }
}
