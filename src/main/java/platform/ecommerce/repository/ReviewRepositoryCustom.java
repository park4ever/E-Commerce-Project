package platform.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import platform.ecommerce.dto.review.ReviewPageRequestDto;
import platform.ecommerce.entity.Review;

public interface ReviewRepositoryCustom {

    Page<Review> searchReviews(ReviewPageRequestDto requestDto, Pageable pageable);
}
