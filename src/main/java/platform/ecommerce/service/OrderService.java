package platform.ecommerce.service;

import platform.ecommerce.dto.CartCheckoutDto;
import platform.ecommerce.dto.OrderResponseDto;
import platform.ecommerce.dto.OrderSaveRequestDto;
import platform.ecommerce.entity.OrderStatus;

import java.util.List;

public interface OrderService {

    Long createOrder(OrderSaveRequestDto orderSaveRequestDto);

    List<OrderResponseDto> findOrdersByMemberId(Long memberId);

    void updateOrderStatus(Long orderId, OrderStatus status);

    void cancelOrder(Long orderId);
}
