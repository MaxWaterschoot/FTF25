package utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class TicketPriceDeserializer extends JsonDeserializer<BigDecimal> {
    @Override
    public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken t = p.getCurrentToken();

        if (t == JsonToken.VALUE_NUMBER_FLOAT || t == JsonToken.VALUE_NUMBER_INT) {
            return p.getDecimalValue().setScale(2, RoundingMode.HALF_UP);
        }
        if (t == JsonToken.VALUE_STRING) {
            String raw = p.getValueAsString();
            if (raw == null || raw.isBlank()) return null;
            String normalized = raw.trim().replace(',', '.');
            try {
                return new BigDecimal(normalized).setScale(2, RoundingMode.HALF_UP);
            } catch (NumberFormatException ex) {
                // nette melding voor verkeerde string
                ctxt.reportInputMismatch(BigDecimal.class, "Invalid price format: '%s'", raw);
                return null; // never reached
            }
        }

        // vervangt mappingException(...)
        ctxt.reportInputMismatch(
                BigDecimal.class,
                "Cannot deserialize price from token %s; expected number or string.",
                t
        );
        return null; // never reached
    }
}
