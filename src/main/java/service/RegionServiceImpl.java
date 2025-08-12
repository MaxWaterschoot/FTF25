package service;

import domain.Region;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.RegionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegionServiceImpl implements RegionService {

    private final RegionRepository regionRepository;

    @Override public List<Region> findAll() { return regionRepository.findAll(); }

    @Override @Transactional
    public Region create(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name required");
        if (regionRepository.existsByNameIgnoreCase(name)) throw new IllegalStateException("Region exists");
        return regionRepository.save(Region.builder().name(name.trim()).build());
    }

    @Override @Transactional
    public Region rename(Long id, String newName) {
        Region r = regionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Region not found"));
        if (regionRepository.existsByNameIgnoreCase(newName)) throw new IllegalStateException("Region exists");
        r.setName(newName.trim());
        return r;
    }

    @Override @Transactional
    public void delete(Long id) { regionRepository.deleteById(id); }
}
