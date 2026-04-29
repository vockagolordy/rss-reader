package org.example.rssreader.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = {
        "org.example.rssreader.service",
        "org.example.rssreader.repository",
        "org.example.rssreader.util",
        "org.example.rssreader.security"
})
public class RootConfig {
}