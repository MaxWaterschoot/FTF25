package dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class FestivalForm {
    private Long festivalId;

    private String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDateTime;

    private Integer availableTickets;
    private BigDecimal ticketPrice;

    private Long category; // categoryId
    private Long location; // locationId

    private List<Long> standhouderIds = new ArrayList<>();
}
