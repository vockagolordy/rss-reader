package org.example.rssreader.service;

import org.example.rssreader.model.User;
import org.example.rssreader.util.AuthProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UserService userService;

    public CurrentUserService(UserService userService) {
        this.userService = userService;
    }

    public User getCurrentUser(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new UsernameNotFoundException("User is not authenticated");
        }

        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            AuthProvider provider = AuthProvider.fromRegistrationId(
                    oauthToken.getAuthorizedClientRegistrationId()
            );
            String providerId = oauthToken.getName();

            return userService.findByProviderAndProviderId(provider, providerId)
                    .orElseThrow(() -> new UsernameNotFoundException(
                            "OAuth2 user not found: " + provider + " / " + providerId
                    ));
        }

        String username = authentication.getName();

        return userService.findLocalByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Local user not found: " + username));
    }
}