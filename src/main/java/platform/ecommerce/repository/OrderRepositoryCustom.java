package platform.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import platform.ecommerce.dto.order.OrderSearchCondition;
import platform.ecommerce.entity.Order;

import java.util.List;

public interface OrderRepositoryCustom {
    Page<Order> findOrdersWithFilters(OrderSearchCondition cond, Long memberId, Pageable pageable);

    Page<Order> searchOrders(String searchKeyword, Pageable pageable);
}
