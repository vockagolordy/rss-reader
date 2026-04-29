package org.example.rssreader.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(
        name = "posts",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_posts_link", columnNames = "link")
        }
)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private long id;

    @Column(length = 500)
    @ToString.Include
    private String title;

    @Column(length = 255)
    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 255)
    private String author;

    @Column(nullable = false, length = 1000)
    @ToString.Include
    private String link;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    @ToString.Exclude
    private Resource resource;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt;

    @Transient
    private String resourceTitle;

    public Post(String title, String category, String description, String author,
                String link, Resource resource, LocalDateTime publishedAt, LocalDateTime addedAt) {
        this.title = title;
        this.category = category;
        this.description = description;
        this.author = author;
        this.link = link;
        this.resource = resource;
        this.publishedAt = publishedAt;
        this.addedAt = addedAt;
    }

    public long getResourceId() {
        return resource != null ? resource.getId() : 0;
    }

    public void setResourceId(long resourceId) {
        if (this.resource == null) {
            this.resource = new Resource();
        }
        this.resource.setId(resourceId);
    }

    public String getResourceTitle() {
        if (resourceTitle != null) {
            return resourceTitle;
        }

        return resource != null ? resource.getTitle() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post post)) return false;
        return id != 0 && id == post.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}