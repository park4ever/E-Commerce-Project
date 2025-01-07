package platform.ecommerce.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import platform.ecommerce.dto.review.ReviewSearchCondition;
import platform.ecommerce.entity.QReview;
import platform.ecommerce.entity.Review;

import java.util.List;

import static platform.ecommerce.entity.QReview.*;

@Slf4j
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ReviewRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Review> findReviewsWithPageable(ReviewSearchCondition cond, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        //상품 ID로 필터링
        if (cond.getItemId() != null) {
            builder.and(review.item.id.eq(cond.getItemId()));
        }

        //SELECT 쿼리 생성
        JPAQuery<Review> query = queryFactory
                .selectFrom(review)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        //정렬 적용
        OrderSpecifier<?> sortOrder = getSortOrder(cond.getSortBy());
        if (sortOrder != null) {
            query.orderBy(sortOrder);
        }

        List<Review> reviews = query.fetch();

        //COUNT 쿼리 생성
        Long total = queryFactory
                .select(review.count())
                .from(review)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(reviews, pageable, total);
    }

    private OrderSpecifier<?> getSortOrder(String sortBy) {
        log.info("정렬 기준 : {}", sortBy);

        if (sortBy == null || sortBy.isEmpty()) {
            return review.createdDate.desc(); //기본값을 최신순으로
        }

        return switch (sortBy) {
            case "ratingDesc" -> review.rating.desc();      //별점 높은 순
            case "ratingAsc" -> review.rating.asc();        //별점 낮은 순
            case "dateDesc" -> review.createdDate.desc();   //최신순
            case "dateAsc" -> review.createdDate.asc();     //오래된순
            default -> review.createdDate.desc();           //기본값을 최신순으로
        };
    }
}
