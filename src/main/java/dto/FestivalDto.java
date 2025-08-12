package dto;

import domain.Festival;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class FestivalDto {
    private Festival festival;
    private int ticketsBought;
}
