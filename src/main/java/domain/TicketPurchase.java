package domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Inschrijvingen/aankopen per user voor een festival.
 * Regels zoals "max 30 per festival" en "max 100 binnen festivalperiode"
 * worden in de service/validator afgedwongen (op aggregates). :contentReference[oaicite:3]{index=3}
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "purchaseId")
@Table(name = "ticket_purchase")
@ToString(exclude = { "festival", "user" })
public class TicketPurchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long purchaseId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "festival_id")
    @JsonIgnore
    private Festival festival;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private AppUser user;

    @Min(1)
    private int quantity;

    @NotNull
    private LocalDateTime purchasedAt;
}
