package org.example.rssreader.service;

import org.example.rssreader.dto.UserRegistrationDto;
import org.example.rssreader.model.User;
import org.example.rssreader.repository.UserRepository;
import org.example.rssreader.util.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(UserRegistrationDto registrationDto) {
        User user = new User();
        user.setUuid(UUID.randomUUID().toString());
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registrationDto.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public Optional<User> authenticate(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPasswordHash())) {
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void addResourceToUser(long userId, long resourceId) {
        userRepository.addResourceToUser(userId, resourceId);
    }

    public void removeResourceFromUser(long userId, long resourceId) {
        userRepository.removeResourceFromUser(userId, resourceId);
    }
}