package org.example.rssreader.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
        name = "resources",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_resources_link", columnNames = "link")
        }
)
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private long id;

    @Column(nullable = false, length = 255)
    @ToString.Include
    private String title;

    @Column(nullable = false, length = 500)
    @ToString.Include
    private String link;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "resources", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<User> users = new HashSet<>();

    @OneToMany(
            mappedBy = "resource",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @ToString.Exclude
    private Set<Post> posts = new HashSet<>();

    public Resource(String title, String link, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.title = title;
        this.link = link;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void addPost(Post post) {
        posts.add(post);
        post.setResource(this);
    }

    public void removePost(Post post) {
        posts.remove(post);
        post.setResource(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Resource resource)) return false;
        return id != 0 && id == resource.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}