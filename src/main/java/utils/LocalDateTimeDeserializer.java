package utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    private static final DateTimeFormatter[] CANDIDATES = new DateTimeFormatter[] {
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,               // 2025-08-11T16:30:00
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),     // 2025-08-11 16:30
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")      // 11/08/2025 16:30
    };

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getValueAsString();
        if (text == null || text.isBlank()) return null;
        for (DateTimeFormatter f : CANDIDATES) {
            try { return LocalDateTime.parse(text, f); } catch (Exception ignored) {}
        }
        // Fallback: laat Jackson een duidelijke fout geven
        throw ctxt.weirdStringException(text, LocalDateTime.class, "Unsupported date-time format");
    }
}
