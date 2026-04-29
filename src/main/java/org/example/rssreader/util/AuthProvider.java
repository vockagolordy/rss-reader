package org.example.rssreader.util;

public enum AuthProvider {

    local,
    github,
    google;

    public static AuthProvider fromRegistrationId(String registrationId) {
        if (registrationId == null || registrationId.isBlank()) {
            throw new IllegalArgumentException("Registration id must not be empty");
        }

        return AuthProvider.valueOf(registrationId.toLowerCase());
    }

    public String getUsernamePrefix() {
        return name() + "_";
    }

    public String getFallbackEmail(String providerId) {
        return name() + "_" + providerId + "@oauth.local";
    }
}