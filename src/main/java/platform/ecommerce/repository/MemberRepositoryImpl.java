package platform.ecommerce.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import platform.ecommerce.entity.Member;
import platform.ecommerce.entity.QMember;

import java.util.List;

import static platform.ecommerce.entity.QMember.*;

public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public MemberRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Member> searchMembers(String keyword, String field, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        //검색 조건
        if (keyword != null && !keyword.isBlank()) {
            switch (field) {
                case "email" -> builder.and(member.email.containsIgnoreCase(keyword));
                case "username" -> builder.and(member.username.containsIgnoreCase(keyword));
                case "phoneNumber" -> builder.and(member.phoneNumber.containsIgnoreCase(keyword));
                case "role" -> builder.and(member.role.stringValue().containsIgnoreCase(keyword));
                case "all" -> builder.and(
                        member.email.containsIgnoreCase(keyword)
                                .or(member.username.containsIgnoreCase(keyword))
                                .or(member.phoneNumber.containsIgnoreCase(keyword))
                                .or(member.role.stringValue().containsIgnoreCase(keyword))
                );
                default -> builder.and(member.username.containsIgnoreCase(keyword));
            }
        }

        // 정렬 조건
        PathBuilder<Member> pathBuilder = new PathBuilder<>(Member.class, member.getMetadata());
        List<OrderSpecifier<Comparable>> orderSpecifiers = pageable.getSort().stream()
                .map(order -> new OrderSpecifier<>(
                        order.isAscending() ? Order.ASC : Order.DESC,
                        pathBuilder.getComparable(order.getProperty(), Comparable.class)
                ))
                .toList();

        //메인 쿼리
        JPAQuery<Member> query = queryFactory
                .selectFrom(member)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        //정렬 적용
        if (!orderSpecifiers.isEmpty()) {
            query.orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]));
        }

        List<Member> members = query.fetch();

        //카운트 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(member.count())
                .from(member)
                .where(builder);

        return PageableExecutionUtils.getPage(members, pageable, countQuery::fetchOne);
    }
}
