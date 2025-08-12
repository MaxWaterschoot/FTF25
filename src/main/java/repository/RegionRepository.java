package repository;

import domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Long> {
    Optional<Region> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
