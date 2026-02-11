package edu.polytech.plateformefilms.controller;

import edu.polytech.plateformefilms.dto.AuthResponse;
import edu.polytech.plateformefilms.dto.LoginRequest;
import edu.polytech.plateformefilms.dto.RegisterRequest;
import edu.polytech.plateformefilms.model.User;
import edu.polytech.plateformefilms.security.JwtUtil;
import edu.polytech.plateformefilms.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        User user = new User(request.username(), request.email(), request.password());
        user = userService.register(user);
        String token = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        String token = jwtUtil.generateToken(auth.getName());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
