package repository;

import domain.Location;
import domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByNameIgnoreCase(String name);
    List<Location> findByRegionOrderByNameAsc(Region region);
    boolean existsByNameIgnoreCase(String name);
}
