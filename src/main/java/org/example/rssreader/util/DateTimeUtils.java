package org.example.rssreader.util;

import org.springframework.format.Formatter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class DateTimeUtils {

    public static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(PATTERN);

    public static class StringToLocalDateTimeConverter implements org.springframework.core.convert.converter.Converter<String, LocalDateTime> {
        @Override
        public LocalDateTime convert(String source) {
            if (source == null || source.trim().isEmpty()) {
                return null;
            }
            return LocalDateTime.parse(source, FORMATTER);
        }
    }

    public static class LocalDateTimeFormatter implements Formatter<LocalDateTime> {
        @Override
        public LocalDateTime parse(String text, Locale locale) {
            return LocalDateTime.parse(text, FORMATTER);
        }

        @Override
        public String print(LocalDateTime object, Locale locale) {
            return object.format(FORMATTER);
        }
    }
}