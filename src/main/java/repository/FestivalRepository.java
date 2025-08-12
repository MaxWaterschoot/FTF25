package repository;

import domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FestivalRepository extends JpaRepository<Festival, Long> {

    /** Overzicht + filters op categorie en streek (region), gesorteerd op datum/tijd. */
    @Query("""
           SELECT f FROM Festival f
           WHERE (:category IS NULL OR f.category = :category)
             AND (:region   IS NULL OR f.location.region = :region)
           ORDER BY f.startDateTime ASC
           """)
    List<Festival> findByCategoryAndRegionOrderByDate(
            @Param("category") Category category,
            @Param("region") Region region);

    /** Voor REST-service: aantal beschikbare tickets (capaciteit-veld). */
    @Query("SELECT f.availableTickets FROM Festival f WHERE f.festivalId = :festivalId")
    int findAvailableTicketsByFestivalId(@Param("festivalId") Long festivalId);

    /** Handig: lijst per categorie of alles op datum. */
    List<Festival> findByCategoryOrderByStartDateTimeAsc(Category category);
    List<Festival> findAllByOrderByStartDateTimeAsc();

    /** DB-controle 1: “geen dubbel festival in de festivalperiode”. */
    boolean existsByNameIgnoreCaseAndStartDateTimeBetween(String name, LocalDateTime start, LocalDateTime end);

    /** Alternatief (optioneel) op locatie binnen periode. */
    boolean existsByLocationAndStartDateTimeBetween(Location location, LocalDateTime start, LocalDateTime end);

    /** DB-controle 2: “zelfde categorie niet op dezelfde dag (ongeacht streek)”. */
    boolean existsByCategoryAndStartDateTimeBetween(Category category, LocalDateTime dayStart, LocalDateTime dayEnd);
}
