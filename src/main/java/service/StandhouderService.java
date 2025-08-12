package service;

import domain.Standhouder;

import java.util.List;

public interface StandhouderService {
    List<Standhouder> findAll();
    Standhouder create(String name);
    Standhouder rename(Long id, String newName);
    void delete(Long id);
}
