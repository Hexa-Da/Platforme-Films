package edu.polytech.plateformefilms.controller;

import edu.polytech.plateformefilms.dto.UserResponse;
import edu.polytech.plateformefilms.model.User;
import edu.polytech.plateformefilms.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Gestion des profils utilisateurs") //@Tag et @Operation pour les documentations
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Récupérer un profil par ID", description = "Retourne les infos publiques d'un utilisateur")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mapToResponse(user));
    }

    @Operation(summary = "Récupérer mon profil", description = "Retourne les infos de l'utilisateur connecté via son JWT")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(@AuthenticationPrincipal UserDetails currentUser) {
        // @AuthenticationPrincipal extrait l'utilisateur du token JWT automatiquement
        User user = userService.findByUsername(currentUser.getUsername());
        return ResponseEntity.ok(mapToResponse(user));
    }

    // Petite méthode utilitaire pour transformer l'Entité en DTO
    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}