package service;

import domain.Standhouder;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.StandhouderRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StandhouderServiceImpl implements StandhouderService {

    private final StandhouderRepository standhouderRepository;

    @Override public List<Standhouder> findAll() { return standhouderRepository.findAllByOrderByNameAsc(); }

    @Override @Transactional
    public Standhouder create(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name required");
        if (standhouderRepository.existsByNameIgnoreCase(name)) throw new IllegalStateException("Standhouder exists");
        return standhouderRepository.save(Standhouder.builder().name(name.trim()).build());
    }

    @Override @Transactional
    public Standhouder rename(Long id, String newName) {
        Standhouder s = standhouderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Standhouder not found"));
        if (standhouderRepository.existsByNameIgnoreCase(newName)) throw new IllegalStateException("Standhouder exists");
        s.setName(newName.trim());
        return s;
    }

    @Override @Transactional
    public void delete(Long id) { standhouderRepository.deleteById(id); }
}
