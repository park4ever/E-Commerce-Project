package platform.ecommerce.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import platform.ecommerce.dto.review.MyReviewQueryDto;
import platform.ecommerce.dto.review.ReviewPageRequestDto;
import platform.ecommerce.dto.review.ReviewQueryDto;
import platform.ecommerce.entity.QItem;
import platform.ecommerce.entity.QMember;
import platform.ecommerce.entity.QReview;
import platform.ecommerce.entity.Review;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.*;
import static platform.ecommerce.entity.QItem.item;
import static platform.ecommerce.entity.QMember.*;
import static platform.ecommerce.entity.QReview.*;

public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ReviewRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
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

    @Override
    public Page<Review> searchReviewsForItem(ReviewQueryDto queryDto) {
        BooleanBuilder builder = new BooleanBuilder();

        //특정 상품 + 공개된 리뷰만
        builder.and(review.item.id.eq(queryDto.getItemId()));
        builder.and(review.isVisible.eq(true));

        //정렬 조건
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        Order direction = queryDto.getDirection() == Direction.ASC ? Order.ASC : Order.DESC;

        try {
            PathBuilder<Review> pathBuilder = new PathBuilder<>(Review.class, review.getMetadata());
            orderSpecifiers.add(new OrderSpecifier<>(
                    direction,
                    pathBuilder.getComparable(queryDto.getSortBy(), Comparable.class)));
        } catch (IllegalArgumentException e) {
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, review.createdDate));
        }

        //메인 쿼리
        List<Review> results = queryFactory
                .selectFrom(review)
                .leftJoin(review.member, member).fetchJoin()
                .where(builder)
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .offset(queryDto.toPageable().getOffset())
                .limit(queryDto.toPageable().getPageSize())
                .fetch();

        //카운트 쿼리
        Long total = Optional.ofNullable(
                queryFactory.select(review.count())
                        .from(review)
                        .where(builder)
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(results, queryDto.toPageable(), total);
    }

    @Override
    public Page<Review> searchMyReviews(Long memberId, MyReviewQueryDto queryDto) {
        BooleanBuilder builder = new BooleanBuilder();

        //로그인한 사용자의 리뷰만
        builder.and(review.member.id.eq(memberId));

        //정렬 조건
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        Order direction = queryDto.getSortDirection() == Direction.ASC ? Order.ASC : Order.DESC;

        try {
            PathBuilder<Review> pathBuilder = new PathBuilder<>(Review.class, review.getMetadata());
            orderSpecifiers.add(new OrderSpecifier<>(
                    direction,
                    pathBuilder.getComparable(queryDto.getSortField(), Comparable.class)
            ));
        } catch (IllegalArgumentException e) {
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, review.createdDate));
        }

        //메인 쿼리
        List<Review> results = queryFactory
                .selectFrom(review)
                .leftJoin(review.item, item).fetchJoin()
                .where(builder)
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .offset(queryDto.toPageable().getOffset())
                .limit(queryDto.toPageable().getPageSize())
                .fetch();

        Long total = Optional.ofNullable(
                queryFactory.select(review.count())
                        .from(review)
                        .where(builder)
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(results, queryDto.toPageable(), total);
    }

    @Override
    public Double getAverageRatingByItemId(Long itemId) {
        return queryFactory
                .select(review.rating.avg())
                .from(review)
                .where(review.item.id.eq(itemId))
                .fetchOne();
    }

    @Override
    public Map<Long, Double> getAverageRatingMapByItemIds(List<Long> itemIds) {
        List<Tuple> result = queryFactory
                .select(review.item.id, review.rating.avg())
                .from(review)
                .where(review.item.id.in(itemIds))
                .groupBy(review.item.id)
                .fetch();

        return result.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(review.item.id),
                        tuple -> Optional.ofNullable(tuple.get(review.rating.avg())).orElse(0.0)
                ));
    }
}