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
}
