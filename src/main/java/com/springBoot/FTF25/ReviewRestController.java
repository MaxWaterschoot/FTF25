package com.springBoot.FTF25;

import domain.AppUser;
import domain.Review;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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

    public record ReviewRequest(int rating, String description) {}

    @PostMapping("/{id}/reviews")
    public Review create(@PathVariable("id") Long festivalId,
                         @RequestBody ReviewRequest req,
                         Principal principal) {
        if (principal == null) throw new IllegalStateException("Authentication required");
        AppUser user = appUserRepository.findByUsername(principal.getName())
               ;
        return reviewService.addReview(festivalId, user.getUserId(), req.rating(), req.description());
    }
}
