package platform.ecommerce.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import platform.ecommerce.dto.coupon.CouponPageRequestDto;
import platform.ecommerce.entity.Coupon;
import platform.ecommerce.entity.QCoupon;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.domain.Sort.*;
import static platform.ecommerce.entity.QCoupon.*;

public class CouponRepositoryImpl implements CouponRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CouponRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<Coupon> searchCoupons(CouponPageRequestDto requestDto, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        String keyword = requestDto.getSearchKeyword();
        String field = requestDto.getSearchField();

        if (keyword != null && !keyword.isBlank()) {
            switch (field) {
                case "name" -> builder.and(coupon.name.containsIgnoreCase(keyword));
                case "type" -> builder.and(coupon.discountType.stringValue().containsIgnoreCase(keyword));
                case "all" -> builder.andAnyOf(
                        coupon.name.containsIgnoreCase(keyword),
                        coupon.discountType.stringValue().containsIgnoreCase(keyword)
                );
            }
        }

        if (requestDto.getIsEnabled() != null) {
            builder.and(coupon.isEnabled.eq(requestDto.getIsEnabled()));
        }

        if (requestDto.getDiscountType() != null) {
            builder.and(coupon.discountType.eq(requestDto.getDiscountType()));
        }

        if (requestDto.getValidFrom() != null) {
            builder.and(coupon.validFrom.goe(requestDto.getValidFrom()));
        }

        if (requestDto.getValidTo() != null) {
            builder.and(coupon.validTo.loe(requestDto.getValidTo()));
        }

        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        String sortField = requestDto.getSortField();
        Direction direction = requestDto.getSortDirection();
        Order querydslOrder = direction == Direction.ASC ? Order.ASC : Order.DESC;

        try {
            PathBuilder<Coupon> pathBuilder = new PathBuilder<>(Coupon.class, coupon.getMetadata());
            orderSpecifiers.add(new OrderSpecifier<>(querydslOrder, pathBuilder.getComparable(sortField, Comparable.class)));
        } catch (IllegalArgumentException e) {
            orderSpecifiers.add(coupon.id.desc());
        }

        List<Coupon> results = queryFactory
                .selectFrom(coupon)
                .where(builder)
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = Optional.ofNullable(
                queryFactory.select(coupon.count())
                        .from(coupon)
                        .where(builder)
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(results, pageable, total);
    }
}
