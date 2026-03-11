package edu.polytech.plateformefilms.security;

import edu.polytech.plateformefilms.model.User;
import edu.polytech.plateformefilms.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final String frontendBaseUrl;

    public OAuth2AuthenticationSuccessHandler(JwtUtil jwtUtil,
                                              UserService userService,
                                              @Value("${app.frontend.base-url:http://localhost:5173}") String frontendBaseUrl) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.frontendBaseUrl = frontendBaseUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (!(authentication instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unsupported authentication type");
            return;
        }

        OAuth2User oAuth2User = oAuth2AuthenticationToken.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.getOrDefault("email", "");
        String name = (String) attributes.getOrDefault("name", "");
        String sub = (String) attributes.getOrDefault("sub", "");

        String username = !email.isEmpty() ? email : ("google_" + sub);

        User user = userService.findOrCreateOAuth2User(username, email, "google");

        String token = jwtUtil.generateToken(user.getUsername());

        String redirectUrl = frontendBaseUrl + "/oauth2/callback?token=" + token;
        response.sendRedirect(redirectUrl);
    }
}

