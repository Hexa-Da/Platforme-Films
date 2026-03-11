package edu.polytech.plateformefilms.security;

import edu.polytech.plateformefilms.model.User;
import edu.polytech.plateformefilms.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class OAuth2AuthenticationSuccessHandlerTest {

    @Test
    void onAuthenticationSuccess_redirectsWithToken() throws Exception {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        UserService userService = mock(UserService.class);
        OAuth2AuthenticationSuccessHandler handler =
                new OAuth2AuthenticationSuccessHandler(jwtUtil, userService, "http://frontend");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        Map<String, Object> attributes = Map.of(
                "email", "test@example.com",
                "name", "Test User",
                "sub", "123456"
        );
        OAuth2User oAuth2User = new DefaultOAuth2User(List.of(), attributes, "sub");
        OAuth2AuthenticationToken authentication = new OAuth2AuthenticationToken(
                oAuth2User, oAuth2User.getAuthorities(), "google");

        User user = new User();
        user.setUsername("test@example.com");
        when(userService.findOrCreateOAuth2User("test@example.com", "test@example.com", "google")).thenReturn(user);
        when(jwtUtil.generateToken("test@example.com")).thenReturn("jwt-token");

        handler.onAuthenticationSuccess(request, response, authentication);

        ArgumentCaptor<String> redirectCaptor = ArgumentCaptor.forClass(String.class);
        verify(response).sendRedirect(redirectCaptor.capture());
        assertThat(redirectCaptor.getValue()).isEqualTo("http://frontend/oauth2/callback?token=jwt-token");
    }
}

