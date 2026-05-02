package org.example.rssreader.dto;

import org.example.rssreader.model.Post;
import org.example.rssreader.model.Resource;

import java.time.LocalDateTime;

public record PostDto(
        long id,
        String title,
        String description,
        String link,
        LocalDateTime publishedAt,
        String resourceTitle
) {

    public static PostDto from(Post post) {
        Resource resource = post.getResource();

        return new PostDto(
                post.getId(),
                post.getTitle(),
                post.getDescription(),
                post.getLink(),
                post.getPublishedAt(),
                resource == null ? null : resource.getTitle()
        );
    }
}