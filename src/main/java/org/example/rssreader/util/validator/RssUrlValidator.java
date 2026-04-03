package org.example.rssreader.util.validator;

import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

@Component
public class RssUrlValidator implements ConstraintValidator<ValidRssUrl, String> {

    @Override
    public void initialize(ValidRssUrl constraintAnnotation) {
    }

    @Override
    public boolean isValid(String url, ConstraintValidatorContext context) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }

        try {
            // Проверяем, что это валидный URL
            URL validUrl = new URL(url);
            validUrl.toURI(); // Проверяем URI синтаксис

            // Проверяем протокол
            String protocol = validUrl.getProtocol();
            if (!"http".equalsIgnoreCase(protocol) && !"https".equalsIgnoreCase(protocol)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("URL must use HTTP or HTTPS protocol")
                        .addConstraintViolation();
                return false;
            }

            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }
}