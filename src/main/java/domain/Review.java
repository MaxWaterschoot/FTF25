package domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/** Reviewscherm + “1 review per user per festival”. :contentReference[oaicite:4]{index=4} */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "reviewId")
@Table(
        name = "review",
        uniqueConstraints = @UniqueConstraint(columnNames = { "festival_id", "user_id" })
)
@ToString(exclude = { "festival", "author" })
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "festival_id")
    @JsonIgnore
    private Festival festival;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private AppUser author;

    @Min(1) @Max(5)
    private int rating;

    @NotBlank
    private String description;

    @NotNull
    private LocalDateTime createdAt;
}
