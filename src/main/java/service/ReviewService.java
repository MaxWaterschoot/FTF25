package service;

import domain.Review;

import java.util.List;

public interface ReviewService {
    Review addReview(Long festivalId, Long userId, int rating, String description);
    List<Review> getReviewsForFestival(Long festivalId);
    double getAverageRating(Long festivalId);
}
