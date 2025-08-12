package com.springBoot.FTF25;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.Authentication;
import service.TicketPurchaseService; // <- jouw service

@Controller
@RequiredArgsConstructor
@RequestMapping("/my")
public class MyTicketsController {

    private final TicketPurchaseService ticketPurchaseService;

    @GetMapping("/tickets")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String myTickets(Authentication auth, Model model) {
        String username = auth.getName();
        model.addAttribute("purchases",
                ticketPurchaseService.findByUsername(username));
        return "my/tickets";
    }
}
