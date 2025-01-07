package platform.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import platform.ecommerce.dto.order.OrderSearchCondition;
import platform.ecommerce.entity.Order;

public interface OrderRepositoryCustom {
    Page<Order> findOrdersWithFilters(OrderSearchCondition cond, Long memberId, Pageable pageable);
}
