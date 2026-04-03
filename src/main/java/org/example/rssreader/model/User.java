package org.example.rssreader.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

public class User {
    private long id;
    private String uuid;
    private String username;
    private String email;
    private String passwordHash;
    private LocalDateTime createdAt;
    private Set<Resource> resources;

    public User() {
    }

    public User(String uuid, String username, String email, String passwordHash, LocalDateTime createdAt, Set<Resource> resources) {
        this.uuid = uuid;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
        this.resources = resources;
    }

    public User(long id, String uuid, String username, String email, String passwordHash, LocalDateTime createdAt, Set<Resource> resources) {
        this.id = id;
        this.uuid = uuid;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
        this.resources = resources;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Set<Resource> getResources() { return resources; }
    public void setResources(Set<Resource> resources) { this.resources = resources; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}