package org.example.rssreader.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.example.rssreader.util.AuthProvider;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_uuid", columnNames = "uuid"),
                @UniqueConstraint(name = "uk_users_username", columnNames = "username"),
                @UniqueConstraint(name = "uk_users_email", columnNames = "email")
        }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private long id;

    @Column(nullable = false, length = 100)
    @ToString.Include
    private String uuid;

    @Column(nullable = false, length = 50)
    @ToString.Include
    private String username;

    @Column(nullable = false, length = 100)
    @ToString.Include
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @ToString.Include
    private AuthProvider provider = AuthProvider.local;

    @Column(name = "provider_id", length = 255)
    @ToString.Include
    private String providerId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_resources",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "resource_id")
    )
    @ToString.Exclude
    private Set<Resource> resources = new HashSet<>();

    public User(String uuid, String username, String email, String passwordHash, LocalDateTime createdAt) {
        this.uuid = uuid;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.provider = AuthProvider.local;
        this.providerId = null;
        this.createdAt = createdAt;
    }

    public void addResource(Resource resource) {
        resources.add(resource);
        resource.getUsers().add(this);
    }

    public void removeResource(Resource resource) {
        resources.remove(resource);
        resource.getUsers().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return id != 0 && id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}