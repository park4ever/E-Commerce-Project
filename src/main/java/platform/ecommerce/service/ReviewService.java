package platform.ecommerce.service;

import org.springframework.data.domain.Page;
import platform.ecommerce.dto.review.*;

import java.util.List;
import java.util.Map;

public interface ReviewService {

    Long writeReview(Long memberId, ReviewRequestDto dto);

    ReviewResponseDto findReview(Long reviewId);

    Page<ReviewResponseDto> searchReviewsForItem(ReviewQueryDto queryDto);

    Page<ReviewResponseDto> searchMyReviews(Long memberId, MyReviewQueryDto queryDto);

    ReviewResponseDto updateReview(Long reviewId, Long memberId, ReviewRequestDto dto);

    Long deleteReview(Long reviewId, Long memberId);

    double getAverageRating(Long itemId);

    Map<Long, Double> getAverageRatingMap(List<Long> itemIds);

    long countReviewsByItemId(Long itemId);
}
