package platform.ecommerce.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import platform.ecommerce.dto.review.ReviewRequestDto;
import platform.ecommerce.dto.review.ReviewResponseDto;
import platform.ecommerce.dto.review.ReviewSearchCondition;

import java.util.List;

public interface ReviewService {

    Long createReview(Long memberId, ReviewRequestDto dto);

    ReviewResponseDto findReview(Long reviewId);

    List<ReviewResponseDto> findReviewsByItemId(Long itemId);

    Page<ReviewResponseDto> findReviewsWithPageable(ReviewSearchCondition cond, Pageable pageable);

    ReviewResponseDto updateReview(Long reviewId, Long memberId, ReviewRequestDto dto);

    Long deleteReview(Long reviewId);

    double calculateAverageRating(Long itemId);

    long countReviewsByItemId(Long itemId);
}
