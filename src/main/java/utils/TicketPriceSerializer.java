package utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/** Schrijf consequent met 2 decimalen (bv. 10.50). */
public class TicketPriceSerializer extends JsonSerializer<BigDecimal> {
    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) { gen.writeNull(); return; }
        BigDecimal scaled = value.setScale(2, RoundingMode.HALF_UP);
        // schrijf als string om altijd 2 decimalen te tonen
        gen.writeString(scaled.toPlainString());
    }
}
