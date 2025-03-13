package platform.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import platform.ecommerce.entity.Review;

import java.time.LocalDateTime;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {

    List<Review> findByItemId(Long itemId);

    long countByItemId(Long itemId);

    long countByCreatedDateAfter(LocalDateTime date);
}
