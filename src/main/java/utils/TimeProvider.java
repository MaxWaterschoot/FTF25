package utils;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface TimeProvider {
    LocalDateTime now();

    default LocalDate today() {
        return now().toLocalDate();
    }
}
