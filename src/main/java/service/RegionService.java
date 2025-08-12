package service;

import domain.Region;

import java.util.List;

public interface RegionService {
    List<Region> findAll();
    Region create(String name);
    Region rename(Long id, String newName);
    void delete(Long id);
}
