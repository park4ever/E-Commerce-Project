package platform.ecommerce.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import platform.ecommerce.dto.member.MemberDetailsDto;
import platform.ecommerce.dto.order.*;

import java.util.List;

public interface OrderService {

    Long placeOrder(OrderSaveRequestDto requestDto);

    Long processOrder(OrderSaveRequestDto dto, Long memberId);

    Page<OrderResponseDto> findMyOrders(Long memberId, OrderPageRequestDto dto);

    OrderResponseDto findOrderById(Long orderId, Long memberId);

    void cancelOrder(Long orderId, Long memberId);

    OrderSaveRequestDto buildSingleOrderDto(MemberDetailsDto member, Long itemOptionId, Integer quantity);

    void updateShippingAddress(OrderModificationDto dto, Long memberId);

    void applyRefundOrExchange(OrderModificationDto dto, Long memberId);
}
