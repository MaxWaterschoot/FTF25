package service;

import domain.AppUser;
import domain.Festival;
import domain.Review;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.*;

import utils.TimeProvider;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final FestivalRepository festivalRepository;
    private final TicketPurchaseRepository ticketPurchaseRepository;
    private final AppUserRepository appUserRepository;
    private final TimeProvider timeProvider;

    @Override
    @Transactional
    public Review addReview(Long festivalId, Long userId, int rating, String description) {
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("Rating must be 1..5");

        Festival festival = festivalRepository.findById(festivalId)
                .orElseThrow(() -> new EntityNotFoundException("Festival not found"));

        // Alleen na het festival reviewen
        if (timeProvider.now().isBefore(festival.getStartDateTime())) {
            throw new IllegalStateException("Review kan pas na het festival.");
        }

        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Moet ingeschreven zijn geweest
        boolean attended = ticketPurchaseRepository.existsByUserAndFestival(user, festival);
        if (!attended) {
            throw new IllegalStateException("Reviewen kan enkel na inschrijving/deelname.");
        }

        // Max 1 review per user per festival
        if (reviewRepository.existsByFestivalAndAuthor(festival, user)) {
            throw new IllegalStateException("Je hebt dit festival al beoordeeld.");
        }

        Review review = Review.builder()
                .festival(festival)
                .author(user)
                .rating(rating)
                .description(description)
                .createdAt(timeProvider.now())
                .build();

        return reviewRepository.save(review);
    }

    @Override
    public List<Review> getReviewsForFestival(Long festivalId) {
        Festival festival = festivalRepository.findById(festivalId)
                .orElseThrow(() -> new EntityNotFoundException("Festival not found"));
        return reviewRepository.findByFestivalOrderByCreatedAtDesc(festival);
    }

    @Override
    public double getAverageRating(Long festivalId) {
        Double avg = reviewRepository.averageRatingForFestival(festivalId);
        return avg == null ? 0.0 : avg;
    }
}
