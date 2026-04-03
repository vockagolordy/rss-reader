package org.example.rssreader.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Resource {
    private long id;
    private String title;
    private String link;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Resource() {
    }

    public Resource(String title, String link, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.title = title;
        this.link = link;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Resource(long id, String title, String link, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.link = link;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resource resource = (Resource) o;
        return id == resource.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Resource{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}