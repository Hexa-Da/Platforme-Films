package edu.polytech.plateformefilms.service;

import edu.polytech.plateformefilms.model.User;
import edu.polytech.plateformefilms.repository.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(User user) {
        if (userRepo.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(java.time.LocalDateTime.now());
        return userRepo.save(user);
    }

    public User findByUsername(String username) {
        return userRepo.findByUsername(username).orElse(null);
    }

    public User getById(Long id) {
        return userRepo.findById(id).orElse(null);
    }

    public User findOrCreateOAuth2User(String username, String email, String provider) {
        if (email != null && !email.isBlank()) {
            return userRepo.findByEmail(email).orElseGet(() -> createOAuth2User(username, email, provider));
        }
        return userRepo.findByUsername(username).orElseGet(() -> createOAuth2User(username, email, provider));
    }

    private User createOAuth2User(String username, String email, String provider) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("oauth2-" + provider));
        user.setCreatedAt(java.time.LocalDateTime.now());
        return userRepo.save(user);
    }
}
