package service;

import domain.AppUser;

import java.util.Optional;

public interface AppUserService {
    Optional<AppUser> findByUsername(String username);
    AppUser requireByUsername(String username);
}
