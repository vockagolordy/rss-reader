package org.example.rssreader.util.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = RssUrlValidator.class)
public @interface ValidRssUrl {
    String message() default "Invalid RSS URL";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}