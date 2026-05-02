package org.example.rssreader.dto;

import org.example.rssreader.model.Resource;

import java.time.LocalDateTime;

public record ResourceResponseDto(
        long id,
        String title,
        String link,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static ResourceResponseDto from(Resource resource) {
        return new ResourceResponseDto(
                resource.getId(),
                resource.getTitle(),
                resource.getLink(),
                resource.getCreatedAt(),
                resource.getUpdatedAt()
        );
    }
}