package domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/** “Streek” voor filtering in de lijst. */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "regionId")
@Table(name = "region", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@ToString(exclude = "locations")
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long regionId;

    @NotBlank
    private String name;

    @OneToMany(mappedBy = "region")
    @JsonIgnore
    private List<Location> locations = new ArrayList<>();
}
