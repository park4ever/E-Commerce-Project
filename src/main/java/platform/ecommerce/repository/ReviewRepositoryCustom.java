package platform.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import platform.ecommerce.dto.review.MyReviewQueryDto;
import platform.ecommerce.dto.review.ReviewPageRequestDto;
import platform.ecommerce.dto.review.ReviewQueryDto;
import platform.ecommerce.entity.Review;

import java.util.List;
import java.util.Map;

public interface ReviewRepositoryCustom {

    Page<Review> searchReviews(ReviewPageRequestDto requestDto, Pageable pageable);

    Page<Review> searchReviewsForItem(ReviewQueryDto queryDto);

    Page<Review> searchMyReviews(Long memberId, MyReviewQueryDto queryDto);

    Double getAverageRatingByItemId(Long itemId);

    Map<Long, Double> getAverageRatingMapByItemIds(List<Long> itemIds);
}
