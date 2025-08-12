package domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "categoryId")
@Table(name = "category", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@ToString(exclude = "festivals")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    /** Voorbeelden: Italiaans, Vegan, Barbecue, ... */
    @NotBlank
    private String name;

    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private List<Festival> festivals = new ArrayList<>();
}
