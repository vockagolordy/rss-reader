package org.example.rssreader.repository;

import org.example.rssreader.model.User;
import org.example.rssreader.util.AuthProvider;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndProvider(String username, AuthProvider provider);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndProvider(String email, AuthProvider provider);

    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @EntityGraph(attributePaths = "resources")
    Optional<User> findWithResourcesById(long id);
}