package platform.ecommerce.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.*;
import org.springframework.util.StringUtils;
import platform.ecommerce.dto.order.OrderPageRequestDto;
import platform.ecommerce.entity.*;
import platform.ecommerce.entity.Order;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.domain.Sort.*;
import static platform.ecommerce.entity.QItem.item;
import static platform.ecommerce.entity.QMember.*;
import static platform.ecommerce.entity.QOrder.*;
import static platform.ecommerce.entity.QOrderItem.orderItem;

public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public OrderRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    /**
     * 관리자용 메서드
     */
    @Override
    public Page<Order> searchOrders(OrderPageRequestDto requestDto, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        //검색 조건
        String keyword = requestDto.getSearchKeyword();
        String field = requestDto.getSearchField();

        if (StringUtils.hasText(keyword)) {
            switch (field) {
                case "username" -> builder.and(order.member.username.containsIgnoreCase(keyword));
                case "email" -> builder.and(order.member.email.containsIgnoreCase(keyword));
                case "itemName" -> builder.and(order.orderItems.any().itemOption.item.itemName.containsIgnoreCase(keyword));
                default -> builder.and(
                        order.member.username.containsIgnoreCase(keyword)
                                .or(order.member.email.containsIgnoreCase(keyword))
                                .or(order.orderItems.any().itemOption.item.itemName.containsIgnoreCase(keyword))
                );
            }
        }

        //정렬 조건
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        String sortField = requestDto.getSortField();
        Direction direction = requestDto.getSortDirection();
        com.querydsl.core.types.Order jpaOrder =
                direction == Direction.ASC ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC;

        try {
            PathBuilder<Order> pathBuilder = new PathBuilder<>(Order.class, order.getMetadata());
            orderSpecifiers.add(new OrderSpecifier<>(jpaOrder, pathBuilder.getComparable(sortField, Comparable.class)));
        } catch (IllegalArgumentException e) {
            orderSpecifiers.add(new OrderSpecifier<>(com.querydsl.core.types.Order.DESC, order.orderDate));
        }

        //메인 쿼리
        List<Order> orders = queryFactory
                .selectFrom(order)
                .leftJoin(order.member, member).fetchJoin()
                .distinct()
                .where(builder)
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        //카운트 쿼리
        Long total = Optional.ofNullable(
                queryFactory.select(order.id.count())
                        .from(order)
                        .where(builder)
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(orders, pageable, total);
    }

    /**
     * 회원용 메서드
     */
    @Override
    public Page<Order> searchMyOrders(Long memberId, OrderPageRequestDto requestDto, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        //로그인한 사용자의 주문만 조회
        builder.and(order.member.id.eq(memberId));

        //검색 조건
        String keyword = requestDto.getSearchKeyword();
        String field = requestDto.getSearchField();

        if (StringUtils.hasText(keyword)) {
            switch (field) {
                case "itemName" -> builder.and(
                        order.orderItems.any().itemOption.item.itemName.containsIgnoreCase(keyword));
                case "status" -> tryAdd(() ->
                        builder.and(order.orderStatus.eq(OrderStatus.valueOf(keyword.toUpperCase()))));
                case "dateFrom" -> tryAdd(() ->
                        builder.and(order.orderDate.goe(LocalDate.parse(keyword).atStartOfDay())));
                case "dateTo" -> tryAdd(() ->
                        builder.and(order.orderDate.loe(LocalDate.parse(keyword).atTime(23, 59, 59))));
                default -> builder.and(
                        order.orderItems.any().itemOption.item.itemName.containsIgnoreCase(keyword));
            }
        }

        //정렬 조건
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        String sortField = requestDto.getSortField();
        Direction direction = requestDto.getSortDirection();
        com.querydsl.core.types.Order jpaOrder =
                direction == Direction.ASC ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC;

        try {
            PathBuilder<Order> pathBuilder = new PathBuilder<>(Order.class, order.getMetadata());
            orderSpecifiers.add(new OrderSpecifier<>(jpaOrder, pathBuilder.getComparable(sortField, Comparable.class)));
        } catch (IllegalArgumentException e) {
            orderSpecifiers.add(new OrderSpecifier<>(com.querydsl.core.types.Order.DESC, order.orderDate));
        }

        //메인 쿼리
        List<Order> orders = queryFactory
                .selectFrom(order)
                .leftJoin(order.member, member).fetchJoin()
                .distinct()
                .where(builder)
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        //카운트 쿼리
        Long total = Optional.ofNullable(
                queryFactory.select(order.id.count())
                        .from(order)
                        .where(builder)
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(orders, pageable, total);
    }

    private void tryAdd(Runnable clause) {
        try {
            clause.run();
        } catch (Exception ignored) {}
    }
}
