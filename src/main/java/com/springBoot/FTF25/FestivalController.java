package com.springBoot.FTF25;

import domain.AppUser;
import domain.Category;
import domain.Festival;
import domain.Region;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import repository.AppUserRepository;
import repository.ReviewRepository;
import repository.TicketPurchaseRepository;
import service.*;
import utils.TimeProvider;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/festivals")
public class FestivalController {

    private final FestivalService festivalService;
    private final CategoryService categoryService;
    private final RegionService regionService;
    private final ReviewService reviewService;
    private final TicketPurchaseRepository ticketPurchaseRepository;
    private final AppUserRepository appUserRepository;
    private final TimeProvider timeProvider;

    @GetMapping
    public String list(@RequestParam(name = "category", required = false) String categoryName,
                       @RequestParam(name = "region", required = false) String regionName,
                       Model model,
                       Principal principal) {

        List<Festival> festivals = festivalService.findByCategoryAndRegion(categoryName, regionName);

        // Remaining tickets per festival (zonder lazy loads)
        Map<Long, Integer> remaining = new HashMap<>();
        for (Festival f : festivals) {
            int purchased = ticketPurchaseRepository.sumPurchasedForFestival(f.getFestivalId());
            remaining.put(f.getFestivalId(), f.getAvailableTickets() - purchased);
        }

        model.addAttribute("festivals", festivals);
        model.addAttribute("remainingTickets", remaining);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("regions", regionService.findAll());
        model.addAttribute("selectedCategory", categoryName == null ? "" : categoryName);
        model.addAttribute("selectedRegion", regionName == null ? "" : regionName);
        return "festivals/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, Principal principal) {
        Festival f = festivalService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Festival not found"));

        int purchasedTotal = ticketPurchaseRepository.sumPurchasedForFestival(id);
        int remainingTickets = f.getAvailableTickets() - purchasedTotal;

        Integer userTicketsBought = null;
        boolean canReview = false;

        if (principal != null) {
            AppUser user = appUserRepository.findByUsername(principal.getName())
                    ;
            userTicketsBought = ticketPurchaseRepository.sumPurchasedByUserForFestival(user.getUserId(), id);

            boolean afterFestivalStart = timeProvider.now().isAfter(f.getStartDateTime());
            boolean attended = ticketPurchaseRepository.existsByUserAndFestival(user, f);
            boolean notYetReviewed = !reviewService.getReviewsForFestival(id).stream()
                    .anyMatch(r -> r.getAuthor().getUserId().equals(user.getUserId()));
            canReview = afterFestivalStart && attended && notYetReviewed;
        }

        model.addAttribute("festival", f);
        model.addAttribute("reviews", reviewService.getReviewsForFestival(id));
        model.addAttribute("averageRating", reviewService.getAverageRating(id));
        model.addAttribute("remainingTickets", remainingTickets);
        model.addAttribute("userTicketsBought", userTicketsBought == null ? 0 : userTicketsBought);
        model.addAttribute("canReview", canReview);

        return "festivals/detail";
    }
}
