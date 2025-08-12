package service;

import domain.Category;

import java.util.List;

public interface CategoryService {
    List<Category> findAll();
    Category create(String name);
    Category rename(Long id, String newName);
    void delete(Long id);
}
