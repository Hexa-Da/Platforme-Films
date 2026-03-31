package edu.polytech.plateformefilms.config;

import edu.polytech.plateformefilms.exception.DuplicateException;
import edu.polytech.plateformefilms.exception.ForbiddenException;
import edu.polytech.plateformefilms.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleResponseStatus_returns_correct_status_and_message() {
        var ex = new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input");
        ResponseEntity<Map<String, Object>> response = handler.handleResponseStatus(ex);

        assertEquals(400, response.getStatusCode().value());
        assertEquals(400, response.getBody().get("status"));
        assertEquals("Invalid input", response.getBody().get("message"));
    }

    @Test
    void handleResponseStatus_withNullReason_returnsDefaultMessage() {
        var ex = new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        ResponseEntity<Map<String, Object>> response = handler.handleResponseStatus(ex);

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Erreur", response.getBody().get("message"));
    }

    @Test
    void handleNotFound_returns404() {
        var ex = new NotFoundException("Film non trouvé");
        ResponseEntity<Map<String, Object>> response = handler.handleNotFound(ex);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("Film non trouvé", response.getBody().get("message"));
    }

    @Test
    void handleDuplicate_returns409() {
        var ex = new DuplicateException("Une critique existe déjà");
        ResponseEntity<Map<String, Object>> response = handler.handleDuplicate(ex);

        assertEquals(409, response.getStatusCode().value());
        assertEquals("Une critique existe déjà", response.getBody().get("message"));
    }

    @Test
    void handleForbidden_returns403() {
        var ex = new ForbiddenException("Vous n'êtes pas l'auteur");
        ResponseEntity<Map<String, Object>> response = handler.handleForbidden(ex);

        assertEquals(403, response.getStatusCode().value());
        assertEquals("Vous n'êtes pas l'auteur", response.getBody().get("message"));
    }

    @Test
    void handleIllegalArgument_returns409() {
        var ex = new IllegalArgumentException("Username already exists");
        ResponseEntity<Map<String, Object>> response = handler.handleIllegalArgument(ex);

        assertEquals(409, response.getStatusCode().value());
        assertEquals("Username already exists", response.getBody().get("message"));
    }

    @Test
    void handleIllegalArgument_withNullMessage_returnsDefaultMessage() {
        var ex = new IllegalArgumentException((String) null);
        ResponseEntity<Map<String, Object>> response = handler.handleIllegalArgument(ex);

        assertEquals(409, response.getStatusCode().value());
        assertEquals("Conflit", response.getBody().get("message"));
    }

    @Test
    void handleBadCredentials_returns401() {
        var ex = new BadCredentialsException("Bad credentials");
        ResponseEntity<Map<String, Object>> response = handler.handleBadCredentials(ex);

        assertEquals(401, response.getStatusCode().value());
        assertEquals("Identifiants incorrects", response.getBody().get("message"));
    }
}
