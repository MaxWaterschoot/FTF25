package service;

import domain.Location;
import domain.Region;

import java.util.List;

public interface LocationService {
    List<Location> findAll();
    List<Location> findByRegion(Long regionId);
    Location create(String name, Long regionId);
    Location rename(Long id, String newName);
    void delete(Long id);
}
