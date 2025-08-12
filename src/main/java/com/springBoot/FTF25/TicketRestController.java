package com.springBoot.FTF25;

import domain.AppUser;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import repository.AppUserRepository;
import service.TicketPurchaseService;

import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/festivals")
public class TicketRestController {

    private final TicketPurchaseService ticketPurchaseService;
    private final AppUserRepository appUserRepository;

    public record PurchaseRequest(int quantity) {}

    @PostMapping("/{id}/purchase")
    public Map<String, Object> purchase(@PathVariable("id") Long festivalId,
                                        @RequestBody PurchaseRequest req,
                                        Principal principal) {
        if (principal == null) {
            throw new IllegalStateException("Authentication required");
        }
        AppUser user = appUserRepository.findByUsername(principal.getName());

        ticketPurchaseService.purchase(festivalId, user.getUserId(), req.quantity());
        int bought = ticketPurchaseService.getTicketsForFestivalByUser(festivalId, user.getUserId());

        return Map.of(
                "festivalId", festivalId,
                "userId", user.getUserId(),
                "userTicketsBought", bought,
                "status", "OK"
        );
    }
}
