package repository;

import domain.AppUser;
import domain.Festival;
import domain.TicketPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface TicketPurchaseRepository extends JpaRepository<TicketPurchase, Long> {

    /** Totaal aangekochte tickets voor een festival (om resterend te berekenen). */
    @Query("SELECT COALESCE(SUM(tp.quantity), 0) FROM TicketPurchase tp WHERE tp.festival.festivalId = :festivalId")
    int sumPurchasedForFestival(@Param("festivalId") Long festivalId);

    /** Per user voor een specifiek festival (regel: max 30 per festival). */
    @Query("""
           SELECT COALESCE(SUM(tp.quantity), 0)
           FROM TicketPurchase tp
           WHERE tp.user.userId = :userId AND tp.festival.festivalId = :festivalId
           """)
    int sumPurchasedByUserForFestival(@Param("userId") Long userId, @Param("festivalId") Long festivalId);

    /** Per user binnen festivalperiode (regel: max 100 binnen periode). */
    @Query("""
           SELECT COALESCE(SUM(tp.quantity), 0)
           FROM TicketPurchase tp
           WHERE tp.user.userId = :userId AND tp.purchasedAt BETWEEN :start AND :end
           """)
    int sumPurchasedByUserInPeriod(@Param("userId") Long userId,
                                   @Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end);

    /** Nodig om te checken of de user ingeschreven was (voor review-rechten). */
    boolean existsByUserAndFestival(AppUser user, Festival festival);
}
