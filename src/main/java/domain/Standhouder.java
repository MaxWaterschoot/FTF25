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
@EqualsAndHashCode(exclude = "standhouderId")
@Table(name = "standhouder")
@ToString(exclude = "festivals")
public class Standhouder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long standhouderId;

    @NotBlank
    private String name;

    @ManyToMany(mappedBy = "standhouders")
    @JsonIgnore
    private List<Festival> festivals = new ArrayList<>();
}
