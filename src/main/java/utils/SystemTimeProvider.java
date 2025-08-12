package utils;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/** Productie-implementatie; in tests kan je een Stub/Fake gebruiken. */
@Component
public class SystemTimeProvider implements TimeProvider {
    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
