package platform.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import platform.ecommerce.dto.order.OrderPageRequestDto;
import platform.ecommerce.dto.order.OrderSearchCondition;
import platform.ecommerce.entity.Order;

import java.util.List;

public interface OrderRepositoryCustom {
    Page<Order> searchOrders(OrderPageRequestDto requestDto, Pageable pageable);
}
