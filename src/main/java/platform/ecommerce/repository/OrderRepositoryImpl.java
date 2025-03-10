package platform.ecommerce.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.*;
import org.springframework.util.StringUtils;
import platform.ecommerce.dto.order.OrderSearchCondition;
import platform.ecommerce.entity.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static platform.ecommerce.entity.QItem.item;
import static platform.ecommerce.entity.QMember.*;
import static platform.ecommerce.entity.QOrder.*;
import static platform.ecommerce.entity.QOrderItem.orderItem;

public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public OrderRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Order> findOrdersWithFilters(OrderSearchCondition cond, Long memberId, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        //기본 필터링 조건
        builder.and(order.member.id.eq(memberId));

        applyOrderFilters(cond, builder);

        List<Order> orders = queryFactory
                .selectFrom(order)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getSortOrder(cond))
                .fetch();

        //COUNT 쿼리 생성
        Long total = Optional.ofNullable(
                queryFactory.select(order.id.count())
                .from(order)
                .where(builder)
                .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(orders, pageable, total);
    }

    @Override
    public Page<Order> searchOrders(String searchKeyword, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(searchKeyword)) {
            builder.and(order.member.username.containsIgnoreCase(searchKeyword)
                    .or(order.member.email.containsIgnoreCase(searchKeyword)));
        }

        List<Order> orders = queryFactory
                .selectFrom(order)
                .leftJoin(order.member, member).fetchJoin()
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(order.orderDate.desc()) //기본 정렬 : 최신순
                .fetch();

        Long total = Optional.ofNullable(
                queryFactory.select(order.id.count())
                        .from(order)
                        .where(builder)
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(orders, pageable, total);
    }

    // 주문 검색 조건 적용 메서드
    private void applyOrderFilters(OrderSearchCondition cond, BooleanBuilder builder) {
        if (cond.getStartDate() != null) {
            builder.and(order.orderDate.goe(cond.getStartDate().atStartOfDay()));
        }
        if (cond.getEndDate() != null) {
            builder.and(order.orderDate.loe(cond.getEndDate().atTime(LocalTime.MAX)));
        }
        if (cond.getStatus() != null) {
            builder.and(order.orderStatus.eq(cond.getStatus()));
        }
        if (cond.getIsPaid() != null) {
            builder.and(order.isPaid.eq(cond.getIsPaid()));
        }
    }

    // 동적 정렬 적용 메서드
    private OrderSpecifier<?> getSortOrder(OrderSearchCondition cond) {
        if ("orderDate".equals(cond.getSortBy())) {
            return cond.isAscending() ? order.orderDate.asc() : order.orderDate.desc();
        } else if ("orderStatus".equals(cond.getSortBy())) {
            return cond.isAscending() ? order.orderStatus.asc() : order.orderStatus.desc();
        } else if ("memberName".equals(cond.getSortBy())) {
            return cond.isAscending() ? order.member.username.asc() : order.member.username.desc();
        } else if ("paymentStatus".equals(cond.getSortBy())) {
            return cond.isAscending() ? order.isPaid.asc() : order.isPaid.desc();
        }
        return order.orderDate.desc(); // 기본 정렬 기준
    }
}
