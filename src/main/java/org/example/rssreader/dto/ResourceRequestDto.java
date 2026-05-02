package org.example.rssreader.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.example.rssreader.util.validator.ValidRssUrl;

public record ResourceRequestDto(

        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title must be less than 255 characters")
        String title,

        @NotBlank(message = "RSS link is required")
        @Size(max = 500, message = "RSS link must be less than 500 characters")
        @ValidRssUrl
        String link
) {
}