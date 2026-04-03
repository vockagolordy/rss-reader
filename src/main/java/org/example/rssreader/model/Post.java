package org.example.rssreader.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Post {
    private long id;
    private String title;
    private String category;
    private String description;
    private String author;
    private String link;
    private long resourceId;
    private LocalDateTime publishedAt;
    private LocalDateTime addedAt;
    private transient String resourceTitle;

    public Post() {
    }

    public Post(String title, String category, String description, String author,
                String link, long resourceId, LocalDateTime publishedAt, LocalDateTime addedAt) {
        this.title = title;
        this.category = category;
        this.description = description;
        this.author = author;
        this.link = link;
        this.resourceId = resourceId;
        this.publishedAt = publishedAt;
        this.addedAt = addedAt;
    }

    public Post(long id, String title, String category, String description, String author,
                String link, long resourceId, LocalDateTime publishedAt, LocalDateTime addedAt) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.description = description;
        this.author = author;
        this.link = link;
        this.resourceId = resourceId;
        this.publishedAt = publishedAt;
        this.addedAt = addedAt;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public long getResourceId() { return resourceId; }
    public void setResourceId(long resourceId) { this.resourceId = resourceId; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }

    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }

    public String getResourceTitle() {  return resourceTitle; }
    public void setResourceTitle(String resourceTitle) {  this.resourceTitle = resourceTitle; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return id == post.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", publishedAt=" + publishedAt +
                '}';
    }
}