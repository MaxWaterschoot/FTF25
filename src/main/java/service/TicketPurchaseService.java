package service;

public interface TicketPurchaseService {
    int getTicketsForFestivalByUser(Long festivalId, Long userId);
    void purchase(Long festivalId, Long userId, int quantity);
}
