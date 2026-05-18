package org.example.rssreader.dto;

import org.example.rssreader.model.Post;
import org.springframework.data.domain.Page;

import java.util.List;

public record FeedDto(
        List<PostDto> posts,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext
) {

    public static FeedDto from(Page<Post> postPage) {
        List<PostDto> posts = postPage.getContent()
                .stream()
                .map(PostDto::from)
                .toList();

        return new FeedDto(
                posts,
                postPage.getNumber(),
                postPage.getSize(),
                postPage.getTotalElements(),
                postPage.getTotalPages(),
                postPage.hasNext()
        );
    }
}
