package org.example.rssreader.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.example.rssreader.util.validator.ValidRssUrl;

public class ResourceDto {

    @NotBlank(message = "Resource title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;

    @NotBlank(message = "RSS URL is required")
    @Size(max = 500, message = "URL must not exceed 500 characters")
    @ValidRssUrl(message = "Invalid RSS URL")
    private String link;

    public ResourceDto() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}