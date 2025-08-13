package com.springBoot.FTF25;

import domain.AppUser;
import domain.Review;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repository.AppUserRepository;
import service.ReviewService;

import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/festivals")
public class ReviewRestController {

    private final ReviewService reviewService;
    private final AppUserRepository appUserRepository;

    public record ReviewRequest(
            @Min(1) @Max(5) int rating,
            @NotBlank String description
    ) {}

    @PostMapping("/{id}/reviews")
    public ResponseEntity<?> create(@PathVariable("id") Long festivalId,
                                    @RequestBody ReviewRequest req,
                                    Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Authentication required"));
        }
        AppUser user = appUserRepository.findByUsername(principal.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "User not found"));
        }

        try {
            Review saved = reviewService.addReview(
                    festivalId, user.getUserId(), req.rating(), req.description()
            );
            // 201 + Location naar lijstpagina
            return ResponseEntity.status(HttpStatus.CREATED)
                    .header("Location", "/festivals/" + festivalId + "/reviews")
                    .body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            // bv. nog niet voorbij / niet ingeschreven / al beoordeeld
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    /** Voor UI: mag de ingelogde user een review schrijven voor dit festival? */
    @GetMapping("/{id}/reviews/can-write")
    public Map<String, Boolean> canWrite(@PathVariable("id") Long festivalId, Principal principal) {
        boolean ok = principal != null && reviewService.canUserReview(festivalId, principal.getName());
        return Map.of("canWrite", ok);
    }
}
