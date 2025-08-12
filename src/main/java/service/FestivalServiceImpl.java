package service;

import domain.*;
import dto.FestivalDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.*;
import utils.TimeProvider;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FestivalServiceImpl implements FestivalService {

    private final FestivalRepository festivalRepository;
    private final CategoryRepository categoryRepository;
    private final RegionRepository regionRepository;
    private final LocationRepository locationRepository;
    private final TicketPurchaseRepository ticketPurchaseRepository;
    private final AppUserRepository appUserRepository;
    private final TimeProvider timeProvider;

    @Override
    public List<FestivalDto> fetchFestivalsWithTickets(String categoryName, String regionName, Principal principal) {
        Category category = (categoryName == null || categoryName.isBlank()) ? null : requireCategory(categoryName);
        Region region = (regionName == null || regionName.isBlank()) ? null : requireRegion(regionName);

        List<Festival> festivals = festivalRepository.findByCategoryAndRegionOrderByDate(category, region);

        int userId = -1;
        if (principal != null) {
            AppUser user = appUserRepository.findByUsername(principal.getName())
                    ;
            userId = Math.toIntExact(user.getUserId());
        }

        final int finalUserId = userId;
        return festivals.stream()
                .map(f -> new FestivalDto(
                        f,
                        (finalUserId == -1)
                                ? 0
                                : ticketPurchaseRepository.sumPurchasedByUserForFestival((long) finalUserId, f.getFestivalId())
                ))
                .toList();
    }

    @Override
    public List<Festival> findByCategoryAndRegion(String categoryName, String regionName) {
        Category category = (categoryName == null || categoryName.isBlank()) ? null : requireCategory(categoryName);
        Region region = (regionName == null || regionName.isBlank()) ? null : requireRegion(regionName);
        return festivalRepository.findByCategoryAndRegionOrderByDate(category, region);
    }

    @Override
    public List<Festival> findAllOrderByDate() {
        return festivalRepository.findAllByOrderByStartDateTimeAsc();
    }

    @Override
    public Optional<Festival> findById(Long id) {
        return festivalRepository.findById(id);
    }

    @Override
    public int getAvailableTickets(Long festivalId) {
        return festivalRepository.findAvailableTicketsByFestivalId(festivalId);
    }

    @Override
    @Transactional
    public Festival create(Festival festival) {
        // Validaties: geen duplicate in periode, en niet dezelfde categorie op dezelfde dag
        LocalDateTime start = festival.getStartDateTime();
        if (start == null) throw new IllegalArgumentException("startDateTime is required");

        LocalDate day = start.toLocalDate();
        LocalDateTime dayStart = day.atStartOfDay();
        LocalDateTime dayEnd = day.atTime(LocalTime.MAX);

        if (festivalRepository.existsByNameIgnoreCaseAndStartDateTimeBetween(
                festival.getName(), start.minusHours(1), start.plusHours(1))) {
            throw new IllegalStateException("Festival met zelfde naam rond dit tijdstip bestaat al.");
        }

        if (festival.getCategory() != null &&
            festivalRepository.existsByCategoryAndStartDateTimeBetween(
                    festival.getCategory(), dayStart, dayEnd)) {
            throw new IllegalStateException("Er bestaat al een festival met dezelfde categorie op deze dag.");
        }

        // Zorg dat relaties bestaan (optioneel, handig bij DTO->Entity mapping)
        if (festival.getLocation() != null) {
            locationRepository.findById(festival.getLocation().getLocationId())
                    .orElseThrow(() -> new EntityNotFoundException("Location not found"));
        }
        if (festival.getCategory() != null) {
            categoryRepository.findById(festival.getCategory().getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        }

        return festivalRepository.save(festival);
    }

    @Override
    @Transactional
    public Festival update(Long id, Festival updated) {
        Festival existing = festivalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Festival not found"));

        LocalDateTime start = updated.getStartDateTime();
        if (start == null) throw new IllegalArgumentException("startDateTime is required");

        existing.setName(updated.getName());
        existing.setStartDateTime(updated.getStartDateTime());
        existing.setAvailableTickets(updated.getAvailableTickets());
        existing.setTicketPrice(updated.getTicketPrice());
        existing.setLocation(updated.getLocation());
        existing.setCategory(updated.getCategory());
        existing.setStandhouders(updated.getStandhouders());

        return existing; // JPA dirty checking
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!festivalRepository.existsById(id)) return;
        festivalRepository.deleteById(id);
    }

    @Override
    public Category requireCategory(String name) {
        return categoryRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + name));
    }

    @Override
    public Region requireRegion(String name) {
        return regionRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new EntityNotFoundException("Region not found: " + name));
    }
}
