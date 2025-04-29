package platform.ecommerce.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import platform.ecommerce.dto.member.MemberDetailsDto;
import platform.ecommerce.dto.order.*;
import platform.ecommerce.entity.OrderStatus;

import java.util.List;

public interface OrderService {

    Long placeOrder(OrderSaveRequestDto requestDto);

    List<OrderResponseDto> findOrdersByMemberId(Long memberId);

    void updateOrderStatus(Long orderId, OrderStatus status);

    void cancelOrder(Long orderId);

    OrderResponseDto findOrderById(Long orderId);

    OrderSaveRequestDto buildSingleOrderDto(MemberDetailsDto member, Long itemOptionId, Integer quantity);

    void updateShippingAddress(OrderModificationDto dto);

    void applyRefundOrExchange(OrderModificationDto dto);

    Page<OrderResponseDto> searchOrders(OrderPageRequestDto requestDto, Pageable pageable);
}
