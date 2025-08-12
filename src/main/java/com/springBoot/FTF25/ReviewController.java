package com.springBoot.FTF25;

import domain.AppUser;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import repository.AppUserRepository;
import service.ReviewService;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final AppUserRepository appUserRepository;

    @PostMapping
    public String add(@RequestParam("festivalId") Long festivalId,
                      @RequestParam("rating") int rating,
                      @RequestParam("description") String description,
                      Principal principal,
                      RedirectAttributes ra) {

        if (principal == null) {
            ra.addAttribute("error", "Je moet ingelogd zijn om te reviewen.");
            return "redirect:/festivals/" + festivalId;
        }

        AppUser user = appUserRepository.findByUsername(principal.getName())
              ;

        try {
            reviewService.addReview(festivalId, user.getUserId(), rating, description);
            ra.addAttribute("success", "Review toegevoegd!");
        } catch (Exception e) {
            ra.addAttribute("error", e.getMessage());
        }
        return "redirect:/festivals/" + festivalId;
    }
}
