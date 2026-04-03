package org.example.rssreader.util;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.example.rssreader.model.Post;
import org.example.rssreader.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.apache.commons.text.StringEscapeUtils;


import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class RSSParser {

    private static final Logger log = LoggerFactory.getLogger(RSSParser.class);

    public List<Post> parseFeed(Resource resource) {
        List<Post> posts = new ArrayList<>();

        try {
            log.info("Parsing: {}", resource.getLink());

            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(new URL(resource.getLink())));

            for (SyndEntry entry : feed.getEntries()) {
                Post post = new Post();
                post.setResourceId(resource.getId());
                post.setTitle(entry.getTitle());
                post.setLink(entry.getLink());

                if (entry.getDescription() != null) {

                    String description = entry.getDescription().getValue();

                    description = Jsoup.parse(description).text();

                    post.setDescription(description);
                }

                Date pubDate = entry.getPublishedDate();
                if (pubDate != null) {
                    post.setPublishedAt(pubDate.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime());
                } else {
                    post.setPublishedAt(LocalDateTime.now());
                }

                post.setAddedAt(LocalDateTime.now());
                posts.add(post);
            }

            log.info("Parsed {} posts from {}", posts.size(), resource.getLink());

        } catch (Exception e) {
            log.error("Failed to parse RSS: {}", resource.getLink(), e);
            throw new RuntimeException("Failed to parse RSS feed: " + e.getMessage());
        }

        return posts;
    }

    public boolean isValidRssFeed(String url) {
        try {
            SyndFeedInput input = new SyndFeedInput();
            input.build(new XmlReader(new URL(url)));
            return true;
        } catch (Exception e) {
            log.debug("Invalid RSS URL: {}", url);
            return false;
        }
    }
}