package org.example.rssreader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class RssReaderApplication {
    public static void main(String[] args) {
        SpringApplication.run(RssReaderApplication.class, args);
    }
}