package domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/** Locatie (uit lijst). Koppelt impliciet een festival aan een “streek/region”. */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "locationId")
@Table(name = "location", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@ToString(exclude = { "region", "festivals" })
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long locationId;

    @NotBlank
    private String name;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    @JsonIgnore
    private Region region;

    @OneToMany(mappedBy = "location")
    @JsonIgnore
    private List<Festival> festivals = new ArrayList<>();
}
