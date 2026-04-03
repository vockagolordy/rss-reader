package org.example.rssreader.exception;

public class RssParseException extends RuntimeException {
    public RssParseException(String message) {
        super(message);
    }

    public RssParseException(String message, Throwable cause) {
        super(message, cause);
    }
}