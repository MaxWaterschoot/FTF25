package dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class FestivalForm {
    private Long festivalId;

    @Pattern(regexp = "^[A-Za-z]{3}.*", message = "{festival.name.prefix}")
    private String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDateTime;

    // codes in form
    @NotNull(message = "{festival.codes.required}")
    private Integer festivalCode1;

    @NotNull(message = "{festival.codes.required}")
    private Integer festivalCode2;

    @Min(value = 50, message = "{festival.tickets.range}")
    @Max(value = 300, message = "{festival.tickets.range}")
    private Integer availableTickets;

    @DecimalMin(value = "10.50", inclusive = true, message = "{festival.price.range}")
    @DecimalMax(value = "40.00", inclusive = false, message = "{festival.price.range}")
    private BigDecimal ticketPrice;

    private Long category; // id
    private Long location; // id

    @Size(min = 1, max = 4, message = "{standhouders.size}")
    private List<Long> standhouderIds = new ArrayList<>();
}
