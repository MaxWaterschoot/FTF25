package service;

import domain.Category;
import domain.Festival;
import domain.Region;
import dto.FestivalDto;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

public interface FestivalService {
    List<FestivalDto> fetchFestivalsWithTickets(String categoryName, String regionName, Principal principal);

    List<Festival> findByCategoryAndRegion(String categoryName, String regionName);
    List<Festival> findAllOrderByDate();
    Optional<Festival> findById(Long id);
    int getAvailableTickets(Long festivalId);

    // Admin CRUD (validaties in service)
    Festival create(Festival festival);
    Festival update(Long id, Festival updated);
    void delete(Long id);

    // Hulpmethodes om entiteiten te vinden (optioneel voor controllers)
    Category requireCategory(String name);
    Region requireRegion(String name);
}
