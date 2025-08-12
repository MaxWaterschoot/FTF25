package domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "festivalId")
@JsonPropertyOrder({
        "festivalId", "name", "startDateTime", "location", "category",
        "availableTickets", "ticketPrice", "standhouders"
})
@Table(name = "festival")
@ToString(exclude = { "standhouders", "purchases", "reviews", "location", "category" })
public class Festival {

    @Id
    @JsonProperty("festivalId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long festivalId;

    @NotBlank
    private String name;

    @JsonSerialize(using = utils.LocalDateTimeSerializer.class)
    @JsonDeserialize(using = utils.LocalDateTimeDeserializer.class)
    private LocalDateTime startDateTime;

    @JsonSerialize(using = utils.TicketPriceSerializer.class)
    @JsonDeserialize(using = utils.TicketPriceDeserializer.class)
    private BigDecimal ticketPrice;

    /** Capaciteit voor dit festival (basis: “Aantal beschikbare tickets”) */
    @Positive
    private int availableTickets;

    /** Extra validatie in validator: strikt even/door 3/verschil < 300. */
    private Integer festivalCode1;
    private Integer festivalCode2;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    @JsonIgnore
    private Location location;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonIgnore
    private Category category;

    /** Max 4 standhouders -> enforced via validator. */
    @ManyToMany
    @JoinTable(
            name = "festival_standhouder",
            joinColumns = @JoinColumn(name = "festival_id"),
            inverseJoinColumns = @JoinColumn(name = "standhouder_id")
    )
    @JsonIgnore
    private List<Standhouder> standhouders = new ArrayList<>();

    /** Inschrijvingen/aankopen (met aantallen) */
    @OneToMany(mappedBy = "festival", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<TicketPurchase> purchases = new ArrayList<>();

    /** Reviews door users (1 per user per festival) */
    @OneToMany(mappedBy = "festival", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Review> reviews = new ArrayList<>();

    /** Niet persistent: nog resterende tickets = capaciteit - som van aangekochte aantallen */
    @Transient
    public int getRemainingTickets() {
        return availableTickets - purchases.stream().mapToInt(TicketPurchase::getQuantity).sum();
    }
}
