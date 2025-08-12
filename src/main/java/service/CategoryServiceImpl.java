package service;

import domain.Category;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.CategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override public List<Category> findAll() { return categoryRepository.findAll(); }

    @Override @Transactional
    public Category create(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name required");
        if (categoryRepository.existsByNameIgnoreCase(name)) throw new IllegalStateException("Category exists");
        return categoryRepository.save(Category.builder().name(name.trim()).build());
    }

    @Override @Transactional
    public Category rename(Long id, String newName) {
        Category c = categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Category not found"));
        if (categoryRepository.existsByNameIgnoreCase(newName)) throw new IllegalStateException("Category exists");
        c.setName(newName.trim());
        return c;
    }

    @Override @Transactional
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}
