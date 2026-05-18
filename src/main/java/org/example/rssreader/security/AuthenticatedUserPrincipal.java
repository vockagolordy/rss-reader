package org.example.rssreader.security;

import org.example.rssreader.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class AuthenticatedUserPrincipal implements UserDetails, OAuth2User {

    private final long id;
    private final String username;
    private final String password;
    private final Map<String, Object> attributes;
    private final String name;

    private AuthenticatedUserPrincipal(long id,
                                       String username,
                                       String password,
                                       Map<String, Object> attributes,
                                       String name) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.attributes = attributes;
        this.name = name;
    }

    public static AuthenticatedUserPrincipal local(User user) {
        return new AuthenticatedUserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getPasswordHash(),
                Map.of(),
                user.getUsername()
        );
    }

    public static AuthenticatedUserPrincipal oauth(User user,
                                                   Map<String, Object> attributes,
                                                   String name) {
        return new AuthenticatedUserPrincipal(
                user.getId(),
                user.getUsername(),
                null,
                attributes,
                name
        );
    }

    public long getId() {
        return id;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return name;
    }
}
