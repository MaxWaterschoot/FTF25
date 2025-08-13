package domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "^[A-Za-z]{3}.*", message = "{festival.name.prefix}")
    private String name;

    @JsonSerialize(using = utils.LocalDateTimeSerializer.class)
    @JsonDeserialize(using = utils.LocalDateTimeDeserializer.class)
    // @WithinFestivalPeriod(start = "2025-01-01T00:00", end = "2025-12-31T23:59",
    //message = "{festival.period}")
    private LocalDateTime startDateTime;

    @JsonSerialize(using = utils.TicketPriceSerializer.class)
    @JsonDeserialize(using = utils.TicketPriceDeserializer.class)
    @DecimalMin(value = "10.50", inclusive = true, message = "{festival.price.range}")
    @DecimalMax(value = "40.00", inclusive = false, message = "{festival.price.range}")
    private BigDecimal ticketPrice;

    @Positive
    @Min(value = 50, message = "{festival.tickets.range}")
    @Max(value = 300, message = "{festival.tickets.range}")
    private int availableTickets;

    @NotNull(message = "{festival.codes.required}")
    private Integer festivalCode1;

    @NotNull(message = "{festival.codes.required}")
    private Integer festivalCode2;
    
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    @JsonIgnore
    private Location location;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonIgnore
    private Category category;

    @ManyToMany
    @JoinTable(
            name = "festival_standhouder",
            joinColumns = @JoinColumn(name = "festival_id"),
            inverseJoinColumns = @JoinColumn(name = "standhouder_id")
    )
    @JsonIgnore
    private List<Standhouder> standhouders = new ArrayList<>();

    @OneToMany(mappedBy = "festival", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<TicketPurchase> purchases = new ArrayList<>();

    @OneToMany(mappedBy = "festival", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Review> reviews = new ArrayList<>();

    @Transient
    public int getRemainingTickets() {
        return availableTickets - purchases.stream().mapToInt(TicketPurchase::getQuantity).sum();
    }
}
