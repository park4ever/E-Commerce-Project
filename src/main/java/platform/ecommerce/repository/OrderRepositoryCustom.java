package platform.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import platform.ecommerce.dto.order.OrderPageRequestDto;
import platform.ecommerce.entity.Order;

import java.util.List;

public interface OrderRepositoryCustom {
    Page<Order> searchOrders(OrderPageRequestDto requestDto, Pageable pageable);
}
