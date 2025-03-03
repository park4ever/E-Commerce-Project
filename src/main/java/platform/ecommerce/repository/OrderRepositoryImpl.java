package platform.ecommerce.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import platform.ecommerce.dto.order.OrderSearchCondition;
import platform.ecommerce.entity.Order;
import platform.ecommerce.entity.QItem;
import platform.ecommerce.entity.QOrder;
import platform.ecommerce.entity.QOrderItem;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static platform.ecommerce.entity.QItem.item;
import static platform.ecommerce.entity.QOrder.*;
import static platform.ecommerce.entity.QOrderItem.orderItem;

@Slf4j
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

        //날짜 필터링
        if (cond.getStartDate() != null) {
            LocalDateTime startDateTime = cond.getStartDate().atStartOfDay();
            builder.and(order.orderDate.goe(startDateTime));
        }

        if (cond.getEndDate() != null) {
            LocalDateTime endDateTime = cond.getEndDate().atTime(LocalTime.MAX);
            builder.and(order.orderDate.loe(endDateTime));
        }

        //주문 상태 필터링
        if (cond.getStatus() != null) {
            builder.and(order.orderStatus.eq(cond.getStatus()));
        }

        //기본 SELECT 쿼리 생성
        JPAQuery<Order> query = queryFactory
                .selectFrom(order)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        //동적 정렬 적용
        OrderSpecifier<?> sortOrder = getSortOrder(cond);
        if (sortOrder != null) {
            query.orderBy(sortOrder);
        } else {
            query.orderBy(order.orderDate.desc());
        }

        List<Order> orders = query.fetch();

        //COUNT 쿼리 생성
        Long total = Optional.ofNullable(queryFactory
                .select(order.id.count())
                .from(order)
                .where(builder)
                .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(orders, pageable, total);
    }

    //정렬 조건 설정 메서드
    private OrderSpecifier<?> getSortOrder(OrderSearchCondition cond) {
        String sortBy = cond.getSortBy();
        log.info("요청된 정렬 기준 : {}", sortBy);

        //기본 정렬 기준 설정
        if (sortBy == null || sortBy.isEmpty()) {
            sortBy = "orderDate"; //기본 정렬 값을 주문 날짜로 설정
        }

        log.info("정렬 기준 : {}, 오름차순 : {}", cond.getSortBy(), cond.isAscending());

        switch (sortBy) {
            case "orderDate" -> {
                return cond.isAscending() ? order.orderDate.asc() : order.orderDate.desc();
            }
            case "orderStatus" -> {
                log.info("주문 상태로 정렬, 방향 : {}", cond.isAscending() ? "ASC" : "DESC");
                return cond.isAscending() ? order.orderStatus.asc() : order.orderStatus.desc();
            }
            case "orderId" -> {
                return cond.isAscending() ? order.id.asc() : order.id.desc();
            }
            default -> {
                return order.orderDate.desc(); //기본적으로 최근 주문이 먼저 나오도록 설정
            }
        }
    }
}
