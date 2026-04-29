package org.example.rssreader.service;

import org.example.rssreader.dto.UserRegistrationDto;
import org.example.rssreader.model.Resource;
import org.example.rssreader.model.User;
import org.example.rssreader.repository.ResourceRepository;
import org.example.rssreader.repository.UserRepository;
import org.example.rssreader.util.AuthProvider;
import org.example.rssreader.util.PasswordEncoder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       ResourceRepository resourceRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.resourceRepository = resourceRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(UserRegistrationDto registrationDto) {
        User user = new User();

        user.setUuid(UUID.randomUUID().toString());
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registrationDto.getPassword()));
        user.setProvider(AuthProvider.local);
        user.setProviderId(null);
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> authenticate(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsernameAndProvider(username, AuthProvider.local);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (user.getPasswordHash() != null
                    && passwordEncoder.matches(password, user.getPasswordHash())) {
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<User> findLocalByUsername(String username) {
        return userRepository.findByUsernameAndProvider(username, AuthProvider.local);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId) {
        return userRepository.findByProviderAndProviderId(provider, providerId);
    }

    @Transactional
    public User findOrCreateOAuthUser(OAuth2AuthenticationToken token) {
        AuthProvider provider = AuthProvider.fromRegistrationId(
                token.getAuthorizedClientRegistrationId()
        );

        OAuth2User oauthUser = token.getPrincipal();

        OAuthUserData userData = extractOAuthUserData(provider, oauthUser, token);

        return userRepository.findByProviderAndProviderId(provider, userData.providerId())
                .orElseGet(() -> createOAuthUser(
                        provider,
                        userData.providerId(),
                        userData.username(),
                        userData.email()
                ));
    }

    @Transactional
    public User createOAuthUser(AuthProvider provider,
                                String providerId,
                                String preferredUsername,
                                String preferredEmail) {
        User user = new User();

        user.setUuid(UUID.randomUUID().toString());
        user.setUsername(makeUniqueUsername(preferredUsername, provider, providerId));
        user.setEmail(makeUniqueEmail(preferredEmail, provider, providerId));
        user.setPasswordHash(null);
        user.setProvider(provider);
        user.setProviderId(providerId);
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Transactional
    public void addResourceToUser(long userId, long resourceId) {
        User user = userRepository.findWithResourcesById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        user.addResource(resource);
        userRepository.save(user);
    }

    @Transactional
    public void removeResourceFromUser(long userId, long resourceId) {
        User user = userRepository.findWithResourcesById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        user.removeResource(resource);
        userRepository.save(user);
    }

    private OAuthUserData extractOAuthUserData(AuthProvider provider,
                                               OAuth2User oauthUser,
                                               OAuth2AuthenticationToken token) {
        if (provider == AuthProvider.github) {
            return extractGithubUserData(oauthUser, token);
        }

        if (provider == AuthProvider.google) {
            return extractGoogleUserData(oauthUser);
        }

        throw new OAuth2AuthenticationException("Unsupported OAuth2 provider: " + provider);
    }

    private OAuthUserData extractGithubUserData(OAuth2User oauthUser,
                                                OAuth2AuthenticationToken token) {
        Object idAttribute = oauthUser.getAttribute("id");

        if (idAttribute == null) {
            throw new OAuth2AuthenticationException("GitHub user id is missing");
        }

        String providerId = idAttribute.toString();

        if (providerId.isBlank() || "null".equals(providerId)) {
            throw new OAuth2AuthenticationException("GitHub user id is missing");
        }

        Object loginAttribute = oauthUser.getAttribute("login");
        Object emailAttribute = oauthUser.getAttribute("email");

        String login = loginAttribute == null ? null : loginAttribute.toString();
        String email = emailAttribute == null ? null : emailAttribute.toString();

        if (email == null || email.isBlank()) {
            email = AuthProvider.github.getFallbackEmail(providerId);
        }

        String username;

        if (login != null && !login.isBlank()) {
            username = AuthProvider.github.getUsernamePrefix() + login;
        } else {
            username = AuthProvider.github.getUsernamePrefix() + providerId;
        }

        return new OAuthUserData(providerId, username, email);
    }

    private OAuthUserData extractGoogleUserData(OAuth2User oauthUser) {
        if (!(oauthUser instanceof OidcUser oidcUser)) {
            throw new OAuth2AuthenticationException("Google user is not an OIDC user");
        }

        String providerId = oidcUser.getSubject();
        String email = oidcUser.getEmail();
        String name = oidcUser.getFullName();

        if (providerId == null || providerId.isBlank()) {
            throw new OAuth2AuthenticationException("Google subject is missing");
        }

        if (email == null || email.isBlank()) {
            email = AuthProvider.google.getFallbackEmail(providerId);
        }

        String username;

        if (email.contains("@")) {
            username = AuthProvider.google.getUsernamePrefix() + email.substring(0, email.indexOf("@"));
        } else if (name != null && !name.isBlank()) {
            username = AuthProvider.google.getUsernamePrefix() + name;
        } else {
            username = AuthProvider.google.getUsernamePrefix() + providerId;
        }

        return new OAuthUserData(providerId, username, email);
    }

    private String loadPrimaryGithubEmail(OAuth2AuthenticationToken token) {
        Object details = token.getDetails();

        /*
         * In this simplified setup we do not keep the OAuth2 access token here.
         * GitHub may return public email in /user. If it does not, we generate
         * a safe fallback email below.
         */
        return null;
    }

    private String makeUniqueUsername(String preferredUsername, AuthProvider provider, String providerId) {
        String username = normalizeUsername(preferredUsername);

        if (!userRepository.existsByUsername(username)) {
            return username;
        }

        String fallback = normalizeUsername(provider.name() + "_" + providerId);

        if (!userRepository.existsByUsername(fallback)) {
            return fallback;
        }

        return normalizeUsername(provider.name() + "_" + providerId + "_" + UUID.randomUUID());
    }

    private String makeUniqueEmail(String preferredEmail, AuthProvider provider, String providerId) {
        if (preferredEmail != null
                && !preferredEmail.isBlank()
                && !userRepository.existsByEmail(preferredEmail)) {
            return preferredEmail;
        }

        String fallback = provider.getFallbackEmail(providerId);

        if (!userRepository.existsByEmail(fallback)) {
            return fallback;
        }

        return provider.name() + "_" + providerId + "_" + UUID.randomUUID() + "@oauth.local";
    }

    private String normalizeUsername(String value) {
        if (value == null || value.isBlank()) {
            return "user_" + UUID.randomUUID();
        }

        String normalized = value
                .toLowerCase()
                .replaceAll("[^a-z0-9_]", "_");

        if (normalized.length() > 50) {
            normalized = normalized.substring(0, 50);
        }

        if (normalized.isBlank()) {
            return "user_" + UUID.randomUUID();
        }

        return normalized;
    }

    private record OAuthUserData(String providerId, String username, String email) {
    }
}