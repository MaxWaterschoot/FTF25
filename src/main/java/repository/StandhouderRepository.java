package repository;

import domain.Standhouder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StandhouderRepository extends JpaRepository<Standhouder, Long> {
    Optional<Standhouder> findByNameIgnoreCase(String name);
    List<Standhouder> findAllByOrderByNameAsc();
    boolean existsByNameIgnoreCase(String name);
}
