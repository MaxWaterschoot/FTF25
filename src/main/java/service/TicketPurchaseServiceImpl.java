package service;

import domain.AppUser;
import domain.Festival;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.AppUserRepository;
import repository.FestivalRepository;
import repository.TicketPurchaseRepository;
import utils.TimeProvider;
import domain.TicketPurchase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketPurchaseServiceImpl implements TicketPurchaseService {

    private static final int MAX_PER_FESTIVAL = 30;
    private static final int MAX_PER_DAY = 100; // interpreteer “festivalperiode” als festivaldag

    private final TicketPurchaseRepository ticketPurchaseRepository;
    private final FestivalRepository festivalRepository;
    private final AppUserRepository appUserRepository;
    private final TimeProvider timeProvider;

    @Override
    public int getTicketsForFestivalByUser(Long festivalId, Long userId) {
        return ticketPurchaseRepository.sumPurchasedByUserForFestival(userId, festivalId);
    }

    @Override
    @Transactional
    public void purchase(Long festivalId, Long userId, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be > 0");

        Festival festival = festivalRepository.findById(festivalId)
                .orElseThrow(() -> new EntityNotFoundException("Festival not found"));
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Festival moet in de toekomst liggen
        if (timeProvider.now().isAfter(festival.getStartDateTime())) {
            throw new IllegalStateException("Inschrijven niet mogelijk: festival is gestart of voorbij.");
        }

        // Per-festival limiet voor de user
        int already = ticketPurchaseRepository.sumPurchasedByUserForFestival(userId, festivalId);
        if (already + quantity > MAX_PER_FESTIVAL) {
            throw new IllegalStateException("Limiet per festival overschreden (max " + MAX_PER_FESTIVAL + ").");
        }

        // Dag-limiet (“festivalperiode” geïnterpreteerd als dag van het festival)
        LocalDate day = festival.getStartDateTime().toLocalDate();
        LocalDateTime dayStart = day.atStartOfDay();
        LocalDateTime dayEnd = day.atTime(LocalTime.MAX);
        int inDay = ticketPurchaseRepository.sumPurchasedByUserInPeriod(userId, dayStart, dayEnd);
        if (inDay + quantity > MAX_PER_DAY) {
            throw new IllegalStateException("Daglimiet overschreden (max " + MAX_PER_DAY + " tickets op festivaldag).");
        }

        // Beschikbaarheid
        int purchasedTotal = ticketPurchaseRepository.sumPurchasedForFestival(festivalId);
        int remaining = festival.getAvailableTickets() - purchasedTotal;
        if (quantity > remaining) {
            throw new IllegalStateException("Niet genoeg tickets beschikbaar. Resterend: " + remaining);
        }

        TicketPurchase tp = TicketPurchase.builder()
                .festival(festival)
                .user(user)
                .quantity(quantity)
                .purchasedAt(timeProvider.now())
                .build();

        ticketPurchaseRepository.save(tp);
    }
}
