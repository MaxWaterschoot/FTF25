package com.springBoot.FTF25;

import domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.CommandLineRunner;
import repository.*;
import utils.TimeProvider;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class InitDataConfig {

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private final TimeProvider timeProvider;

    @Bean
    CommandLineRunner initData(CategoryRepository categoryRepository,
                               RegionRepository regionRepository,
                               LocationRepository locationRepository,
                               StandhouderRepository standhouderRepository,
                               FestivalRepository festivalRepository,
                               AppUserRepository appUserRepository) {
        return args -> {
            // Seed alleen bij lege DB
            if (categoryRepository.count() > 0 || festivalRepository.count() > 0) {
                return;
            }

            // Categories
            Category vegan = categoryRepository.save(Category.builder().name("Vegan").build());
            Category bbq = categoryRepository.save(Category.builder().name("Barbecue").build());
            Category italian = categoryRepository.save(Category.builder().name("Italiaans").build());
            Category street = categoryRepository.save(Category.builder().name("Streetfood").build());

            // Regions
            Region ovl = regionRepository.save(Region.builder().name("Oost-Vlaanderen").build());
            Region wvl = regionRepository.save(Region.builder().name("West-Vlaanderen").build());
            Region ant = regionRepository.save(Region.builder().name("Antwerpen").build());
            Region lim = regionRepository.save(Region.builder().name("Limburg").build());

            // Locations (koppelen aan regio)
            Location gent = locationRepository.save(Location.builder().name("Gent Korenmarkt").region(ovl).build());
            Location brugge = locationRepository.save(Location.builder().name("Brugge Markt").region(wvl).build());
            Location antwerpen = locationRepository.save(Location.builder().name("Antwerpen Groenplaats").region(ant).build());
            Location hasselt = locationRepository.save(Location.builder().name("Hasselt Kolonel Dusart").region(lim).build());

            // Standhouders
            Standhouder s1 = standhouderRepository.save(Standhouder.builder().name("La Pasta Nostra").build());
            Standhouder s2 = standhouderRepository.save(Standhouder.builder().name("Vegan Vibes").build());
            Standhouder s3 = standhouderRepository.save(Standhouder.builder().name("BBQ Brothers").build());
            Standhouder s4 = standhouderRepository.save(Standhouder.builder().name("StreetTaste").build());
            Standhouder s5 = standhouderRepository.save(Standhouder.builder().name("Sweet Wheels").build());

            // Users
            AppUser admin = AppUser.builder()
                    .username("admin")
                    .password(encoder.encode("admin"))
                    .enabled(true)
                    .roles(Set.of(Role.ADMIN))
                    .build();
            AppUser user = AppUser.builder()
                    .username("user")
                    .password(encoder.encode("user"))
                    .enabled(true)
                    .roles(Set.of(Role.USER))
                    .build();
            appUserRepository.saveAll(List.of(admin, user));

            // Helper voor datum/tijd (binnen 2025-periode)
            LocalDateTime now = timeProvider.now().withSecond(0).withNano(0);

            // Festivals (codes + geldige prijzen)
            Festival f1 = Festival.builder()
                    .name("Gentse Foodtruck Fiesta")
                    .startDateTime(now.plusDays(10).withHour(17).withMinute(0))
                    .availableTickets(250)                          // 50..300
                    .ticketPrice(new BigDecimal("12.50"))           // >=10.50, <40
                    .festivalCode1(100)                             // even, >0
                    .festivalCode2(102)                             // %3==0, |diff|<300
                    .location(gent)
                    .category(italian)
                    .standhouders(List.of(s1, s5))
                    .build();

            Festival f2 = Festival.builder()
                    .name("Brugge Vegan Street")
                    .startDateTime(now.plusDays(12).withHour(16).withMinute(0))
                    .availableTickets(200)
                    .ticketPrice(new BigDecimal("10.50"))           // was 10.00 -> 10.50
                    .festivalCode1(200)
                    .festivalCode2(198)
                    .location(brugge)
                    .category(vegan)
                    .standhouders(List.of(s2, s4))
                    .build();

            Festival f3 = Festival.builder()
                    .name("Antwerp BBQ Bash")
                    .startDateTime(now.plusDays(15).withHour(18).withMinute(30))
                    .availableTickets(300)
                    .ticketPrice(new BigDecimal("14.00"))
                    .festivalCode1(300)
                    .festivalCode2(294)
                    .location(antwerpen)
                    .category(bbq)
                    .standhouders(List.of(s3, s4))
                    .build();

            Festival f4 = Festival.builder()
                    .name("Hasselt Street Bites")
                    .startDateTime(now.plusDays(20).withHour(15).withMinute(0))
                    .availableTickets(180)
                    .ticketPrice(new BigDecimal("11.00"))           // was 9.50 -> 11.00
                    .festivalCode1(120)
                    .festivalCode2(117)
                    .location(hasselt)
                    .category(street)
                    .standhouders(List.of(s4, s2))
                    .build();

            festivalRepository.saveAll(List.of(f1, f2, f3, f4));
        };
    }
}
