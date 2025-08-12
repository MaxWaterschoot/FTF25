package repository;

import domain.AppUser;
import domain.Festival;
import domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    /** Reviews gesorteerd (je kiest de volgorde; hier recent eerst). */
    List<Review> findByFestivalOrderByCreatedAtDesc(Festival festival);

    /** Gemiddelde score voor detail/overzicht. */
    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.festival.festivalId = :festivalId")
    Double averageRatingForFestival(Long festivalId);

    /** “1 review per user per festival”. */
    boolean existsByFestivalAndAuthor(Festival festival, AppUser author);

    Optional<Review> findByFestivalAndAuthor(Festival festival, AppUser author);
}
