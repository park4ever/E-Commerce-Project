package platform.ecommerce.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import platform.ecommerce.dto.item.ItemSearchCondition;
import platform.ecommerce.entity.Item;
import platform.ecommerce.entity.QItem;
import platform.ecommerce.entity.QOrderItem;
import platform.ecommerce.entity.QReview;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static platform.ecommerce.entity.QItem.*;
import static platform.ecommerce.entity.QOrderItem.orderItem;
import static platform.ecommerce.entity.QReview.review;

public class ItemRepositoryImpl implements ItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ItemRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Item> findItemsByCondition(ItemSearchCondition cond, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        //상품명 검색 조건
        if (cond.getItemName() != null && !cond.getItemName().isEmpty()) {
            builder.and(item.itemName.containsIgnoreCase(cond.getItemName()));
        }

        //가격 범위 검색 조건
        if (cond.getMinPrice() != null && cond.getMaxPrice() != null) {
            builder.and(item.price.between(cond.getMinPrice(), cond.getMaxPrice()));
        }

        //기본 SELECT 쿼리 생성(상품 리스트 가져오기)
        List<Item> items = queryFactory
                .selectFrom(item)
                .where(builder)
                .orderBy(getSortOrder(cond))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        //COUNT 쿼리 생성(전체 결과 수 가져오기)
        Long total = Optional.ofNullable(queryFactory
                .select(item.id.count())
                .from(item)
                .where(builder)
                .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(items, pageable, total);
    }

    @Override
    public List<Item> searchItems(String searchKeyword, Sort sort) {
        OrderSpecifier<?> orderSpecifier = item.createdDate.desc(); // 기본 정렬: 최신순

        // 정렬 조건 설정
        if (sort != null && sort.isSorted()) {
            for (Sort.Order order : sort) {
                switch (order.getProperty()) {
                    case "price":
                        orderSpecifier = order.isAscending() ? item.price.asc() : item.price.desc();
                        break;
                    case "stockQuantity":
                        orderSpecifier = order.isAscending() ? item.stockQuantity.asc() : item.stockQuantity.desc();
                        break;
                }
            }
        }

        // 검색 조건: 상품명 OR 설명에 검색어 포함
        BooleanBuilder builder = new BooleanBuilder();
        if (StringUtils.hasText(searchKeyword)) {
            builder.or(item.itemName.containsIgnoreCase(searchKeyword))
                    .or(item.description.containsIgnoreCase(searchKeyword));
        }

        return queryFactory
                .selectFrom(item)
                .where(builder)
                .orderBy(orderSpecifier)
                .fetch();
    }

    //정렬 조건 설정 메서드
    private OrderSpecifier<?> getSortOrder(ItemSearchCondition cond) {
        if ("priceAsc".equals(cond.getSortBy())) {
            return item.price.asc();
        } else if ("priceDesc".equals(cond.getSortBy())) {
            return item.price.desc();
        } else if ("name".equals(cond.getSortBy())) {
            return item.itemName.asc();
        }
        return item.id.asc(); // 기본 정렬 조건
    }

    @Override
    public int getTotalSalesByItemId(Long itemId) {
        Integer totalSales = queryFactory
                .select(orderItem.count.sum())
                .from(orderItem)
                .where(orderItem.item.id.eq(itemId))
                .fetchOne();

        return totalSales != null ? totalSales : 0;
    }
}
