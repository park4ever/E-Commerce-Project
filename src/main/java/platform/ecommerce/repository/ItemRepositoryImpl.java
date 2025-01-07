package platform.ecommerce.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import platform.ecommerce.dto.item.ItemSearchCondition;
import platform.ecommerce.entity.Item;
import platform.ecommerce.entity.QItem;

import java.util.List;

import static platform.ecommerce.entity.QItem.*;

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

        //카테고리 검색 조건
//        if (cond.getCategory() != null) {
//            builder.and(item.category.eq(cond.getCategory()));
//        } //TODO DELETE

        //기본 SELECT 쿼리 생성(상품 리스트 가져오기)
        List<Item> items = queryFactory
                .selectFrom(item)
                .where(builder)
                .orderBy(getSortOrder(cond))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        //COUNT 쿼리 생성(전체 결과 수 가져오기)
        Long total = queryFactory
                .select(item.count())
                .from(item)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(items, pageable, total);
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
}
