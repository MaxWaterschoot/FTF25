package com.springBoot.FTF25;

import dto.ReviewForm;
import domain.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import repository.AppUserRepository;
import repository.FestivalRepository;
import service.ReviewService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/festivals/{festivalId}/reviews")
@PreAuthorize("hasRole('USER')")
public class ReviewController {

    private final ReviewService reviewService;
    private final FestivalRepository festivalRepo;
    private final AppUserRepository appUserRepository; 

    @GetMapping
    public String list(@PathVariable Long festivalId, Authentication auth, Model model) {
        var festival = festivalRepo.findById(festivalId).orElseThrow();
        model.addAttribute("festival", festival);
        model.addAttribute("reviews", reviewService.getReviewsForFestival(festivalId));
        model.addAttribute("avgRating", reviewService.getAverageRating(festivalId));

        boolean canWrite = auth != null && auth.isAuthenticated()
                && reviewService.canUserReview(festivalId, auth.getName());
        model.addAttribute("canWriteReview", canWrite);

        return "festivals/reviews";
    }

    @GetMapping("/new")
    public String newForm(@PathVariable Long festivalId, Authentication auth, Model model) {
        if (auth == null || !auth.isAuthenticated()
                || !reviewService.canUserReview(festivalId, auth.getName())) {
            return "redirect:/festivals/{festivalId}/reviews";
        }
        model.addAttribute("festival", festivalRepo.findById(festivalId).orElseThrow());
        model.addAttribute("form", new ReviewForm()); 
        return "festivals/review-form";
    }

    
    @PostMapping
    public String createFallback(@PathVariable Long festivalId,
                                 @ModelAttribute("form") ReviewForm form,
                                 Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }
        AppUser user = appUserRepository.findByUsername(auth.getName());
        if (user == null) {
            return "redirect:/login";
        }

        int rating = form.getRating();
        String description = form.getDescription();

        try {
            reviewService.addReview(festivalId, user.getUserId(), rating, description);
        } catch (RuntimeException ex) {
        }
        return "redirect:/festivals/{festivalId}/reviews";
    }
    
    
}
