package service;

import domain.TicketPurchase;
import java.util.List;

public interface TicketPurchaseService {
    int getTicketsForFestivalByUser(Long festivalId, Long userId);
    void purchase(Long festivalId, Long userId, int quantity);
    List<TicketPurchase> findByUsername(String username); // <— NIEUW
}
