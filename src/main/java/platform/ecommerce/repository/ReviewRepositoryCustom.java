package platform.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import platform.ecommerce.dto.review.ReviewSearchCondition;
import platform.ecommerce.entity.Review;

public interface ReviewRepositoryCustom {

    Page<Review> findReviewsWithPageable(ReviewSearchCondition cond, Pageable pageable);
}
