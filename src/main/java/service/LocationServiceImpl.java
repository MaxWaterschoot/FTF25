package service;

import domain.Location;
import domain.Region;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.LocationRepository;
import repository.RegionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final RegionRepository regionRepository;

    @Override public List<Location> findAll() { return locationRepository.findAll(); }

    @Override public List<Location> findByRegion(Long regionId) {
        Region region = regionRepository.findById(regionId)
                .orElseThrow(() -> new EntityNotFoundException("Region not found"));
        return locationRepository.findByRegionOrderByNameAsc(region);
    }

    @Override @Transactional
    public Location create(String name, Long regionId) {
        Region region = regionRepository.findById(regionId)
                .orElseThrow(() -> new EntityNotFoundException("Region not found"));
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name required");
        if (locationRepository.existsByNameIgnoreCase(name)) throw new IllegalStateException("Location exists");
        return locationRepository.save(Location.builder().name(name.trim()).region(region).build());
    }

    @Override @Transactional
    public Location rename(Long id, String newName) {
        Location l = locationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Location not found"));
        if (locationRepository.existsByNameIgnoreCase(newName)) throw new IllegalStateException("Location exists");
        l.setName(newName.trim());
        return l;
    }

    @Override @Transactional
    public void delete(Long id) { locationRepository.deleteById(id); }
}
