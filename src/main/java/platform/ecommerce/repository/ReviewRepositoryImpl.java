package platform.ecommerce.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.parameters.P;
import org.springframework.util.StringUtils;
import platform.ecommerce.dto.review.ReviewPageRequestDto;
import platform.ecommerce.dto.review.ReviewSearchCondition;
import platform.ecommerce.entity.QItem;
import platform.ecommerce.entity.QMember;
import platform.ecommerce.entity.QReview;
import platform.ecommerce.entity.Review;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.domain.Sort.*;
import static platform.ecommerce.entity.QItem.item;
import static platform.ecommerce.entity.QMember.*;
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
                .leftJoin(review.item, item).fetchJoin()
                .leftJoin(review.member, member).fetchJoin()
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        //정렬 적용
        OrderSpecifier<?> sortOrder = getSortOrder(cond.getSortBy());
        if (sortOrder != null) {
            query.orderBy(sortOrder);
        } else {
            query.orderBy(review.createdDate.desc());
        }

        List<Review> reviews = query.fetch();

        //COUNT 쿼리 생성
        Long total = Optional.ofNullable(queryFactory
                .select(review.id.count())
                .from(review)
                .where(builder)
                .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(reviews, pageable, total);
    }

    @Override
    public Page<Review> searchReviews(ReviewPageRequestDto requestDto, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        String keyword = requestDto.getSearchKeyword();
        String field = requestDto.getSearchField();

        //검색 조건
        if (StringUtils.hasText(keyword)) {
            switch (field) {
                case "memberEmail" -> builder.and(review.member.email.containsIgnoreCase(keyword));
                case "memberName" -> builder.and(review.member.username.containsIgnoreCase(keyword));
                case "itemName" -> builder.and(review.item.itemName.containsIgnoreCase(keyword));
                case "content" -> builder.and(review.content.containsIgnoreCase(keyword));
                default -> builder.and(
                        review.member.email.containsIgnoreCase(keyword)
                                .or(review.member.username.containsIgnoreCase(keyword))
                                .or(review.item.itemName.containsIgnoreCase(keyword))
                                .or(review.content.containsIgnoreCase(keyword))
                );
            }
        }

        //정렬 조건
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        String sortField = requestDto.getSortField();
        Direction direction = requestDto.getSortDirection();
        Order orderDirection = direction == Direction.ASC ? Order.ASC : Order.DESC;

        try {
            PathBuilder<Review> pathBuilder = new PathBuilder<>(Review.class, review.getMetadata());
            orderSpecifiers.add(new OrderSpecifier<>(orderDirection, pathBuilder.getComparable(sortField, Comparable.class)));
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 정렬 필드명 : {}", sortField);
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, review.createdDate));
        }

        //메인 쿼리
        List<Review> reviews = queryFactory
                .selectFrom(review)
                .leftJoin(review.item, item).fetchJoin()
                .leftJoin(review.member, member).fetchJoin()
                .where(builder)
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        //카운트 쿼리
        Long total = Optional.ofNullable(
                queryFactory.select(review.id.count())
                        .from(review)
                        .where(builder)
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(reviews, pageable, total);
    }

    private OrderSpecifier<?> getSortOrder(String sortBy) {
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
