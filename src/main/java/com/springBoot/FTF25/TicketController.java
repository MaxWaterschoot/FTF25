package com.springBoot.FTF25;

import domain.AppUser;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import repository.AppUserRepository;
import service.TicketPurchaseService;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tickets")
public class TicketController {

    private final TicketPurchaseService ticketPurchaseService;
    private final AppUserRepository appUserRepository;

    @PostMapping("/buy/{festivalId}")
    public String buy(@PathVariable Long festivalId,
                      @RequestParam("quantity") int quantity,
                      Principal principal,
                      RedirectAttributes ra) {
        if (principal == null) {
            ra.addAttribute("error", "Je moet ingelogd zijn om tickets te kopen.");
            return "redirect:/festivals/" + festivalId;
        }

        AppUser user = appUserRepository.findByUsername(principal.getName());

        try {
            ticketPurchaseService.purchase(festivalId, user.getUserId(), quantity);
            ra.addAttribute("success", "Tickets gekocht!");
        } catch (Exception e) {
            ra.addAttribute("error", e.getMessage());
        }
        return "redirect:/festivals/" + festivalId;
    }
}
