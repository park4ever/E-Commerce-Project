package platform.ecommerce.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPAExpressions;
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
import platform.ecommerce.dto.item.ItemPageRequestDto;
import platform.ecommerce.entity.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static platform.ecommerce.entity.QItem.*;
import static platform.ecommerce.entity.QItemOption.*;
import static platform.ecommerce.entity.QOrderItem.orderItem;
import static platform.ecommerce.entity.QReview.review;

@Slf4j
public class ItemRepositoryImpl implements ItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ItemRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Item> searchItems(ItemPageRequestDto requestDto, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        //검색 필드에 따른 조건 처리
        String keyword = requestDto.getSearchKeyword();
        String field = requestDto.getSearchField();

        if ("all".equals(field)) {
            builder.andAnyOf(
                    item.itemName.containsIgnoreCase(keyword),
                    item.description.containsIgnoreCase(keyword),
                    item.price.stringValue().containsIgnoreCase(keyword),
                    item.category.stringValue().containsIgnoreCase(keyword)
            );
        } else if ("itemName".equals(field)) {
            builder.and(item.itemName.containsIgnoreCase(keyword));
        } else if ("description".equals(field)) {
            builder.and(item.description.containsIgnoreCase(keyword));
        } else if ("price".equals(field)) {
            builder.and(item.price.stringValue().containsIgnoreCase(keyword));
        } else if ("category".equals(field)) {
            builder.and(item.category.stringValue().containsIgnoreCase(keyword));
        }

        // 가격 범위 조건 처리
        Integer priceMin = requestDto.getPriceMin();
        Integer priceMax = requestDto.getPriceMax();
        if (priceMin != null && priceMax != null && priceMin >= 0 && priceMax >= 0 && priceMin <= priceMax) {
            builder.and(item.price.between(priceMin, priceMax));
        }

        //카테고리 조건
        if (requestDto.getCategory() != null) {
            builder.and(item.category.eq(requestDto.getCategory()));
        }

        // 정렬 조건 처리
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        String sortBy = requestDto.getSortField();
        String direction = String.valueOf(requestDto.getSortDirection());
        Order order = "asc".equalsIgnoreCase(direction) ? Order.ASC : Order.DESC;

        //stockQuantity일 경우 별도 서브쿼리 정렬 처리
        if ("stockQuantity".equals(sortBy)) {
            QItemOption subOption = new QItemOption("subOption");

            orderSpecifiers.add(new OrderSpecifier<>(order,
                    JPAExpressions.select(subOption.stockQuantity.sum())
                            .from(subOption)
                            .where(subOption.item.eq(item))
            ));
        } else {
            //기본 필드 정렬 처리
            PathBuilder<Item> pathBuilder = new PathBuilder<>(Item.class, item.getMetadata());
            try {
                orderSpecifiers.add(new OrderSpecifier<>(order, pathBuilder.getComparable(sortBy, Comparable.class)));
            } catch (IllegalArgumentException e) {
                log.warn("잘못된 정렬 필드명 : {}, 기본값으로 ID 기준 정렬", sortBy);
                orderSpecifiers.add(item.id.asc());
            }
        }

        //재고 범위 조건 처리
        Integer stockMin = requestDto.getStockMin();
        Integer stockMax = requestDto.getStockMax();
        BooleanBuilder havingBuilder = new BooleanBuilder();
        if (stockMin != null && stockMax != null && stockMin <= stockMax) {
            havingBuilder.and(itemOption.stockQuantity.sum().goe(stockMin)
                    .and(itemOption.stockQuantity.sum().loe(stockMax)));
        }

        // 메인 쿼리
        List<Item> items = queryFactory
                .select(item)
                .from(item)
                .leftJoin(item.itemOptions, itemOption)
                .where(builder)
                .groupBy(item.id)
                .having(havingBuilder)
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 카운트 쿼리
        int total = queryFactory
                .select(item.id)
                .from(item)
                .leftJoin(item.itemOptions, itemOption)
                .where(builder)
                .groupBy(item.id)
                .having(havingBuilder)
                .fetch().size();

        return new PageImpl<>(items, pageable, total);
    }

    @Override
    public int getTotalSalesByItemId(Long itemId) {
        Integer totalSales = queryFactory
                .select(orderItem.count.sum())
                .from(orderItem)
                .where(orderItem.itemOption.item.id.eq(itemId))
                .fetchOne();

        return totalSales != null ? totalSales : 0;
    }
}
