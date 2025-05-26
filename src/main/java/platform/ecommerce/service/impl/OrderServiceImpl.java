package platform.ecommerce.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import platform.ecommerce.dto.member.MemberDetailsDto;
import platform.ecommerce.dto.order.*;
import platform.ecommerce.entity.*;
import platform.ecommerce.repository.ItemOptionRepository;
import platform.ecommerce.repository.MemberCouponRepository;
import platform.ecommerce.repository.MemberRepository;
import platform.ecommerce.repository.OrderRepository;
import platform.ecommerce.service.OrderService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static platform.ecommerce.entity.OrderStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemOptionRepository itemOptionRepository;
    private final MemberCouponRepository memberCouponRepository;

    @Override
    public Long placeOrder(OrderSaveRequestDto requestDto, int discountAmount) {
        if (requestDto.getOrderItemDto() == null || requestDto.getOrderItemDto().isEmpty()) {
            throw new IllegalArgumentException("주문 상품이 비어 있습니다.");
        }

        Order order = Order.builder()
                .member(getMemberInfo(requestDto.getMemberId()))
                .orderDate(LocalDateTime.now())
                .orderStatus(PROCESSED)
                .shippingAddress(requestDto.convertToShippingAddress())
                .paymentMethod(requestDto.getPaymentMethod())
                .build();

        MemberCoupon coupon = memberCouponRepository.findById(requestDto.getMemberCouponId())
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));

        order.applyDiscount(coupon, discountAmount);

        for (OrderItemDto dto : requestDto.getOrderItemDto()) {
            ItemOption option = itemOptionRepository.findById(dto.getItemOptionId())
                    .orElseThrow(() -> new IllegalArgumentException("상품 옵션을 찾을 수 없습니다."));

            if (option.isSoldOut() || option.getStockQuantity() < dto.getCount()) {
                throw new IllegalArgumentException("해당 옵션의 재고가 부족합니다.");
            }

            OrderItem orderItem = dto.toOrderItem(option, order);
            order.addOrderItem(orderItem);
            option.removeStock(dto.getCount());
        }

        return orderRepository.save(order).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> findOrdersByMemberId(Long memberId) {
        Member member = getMemberInfo(memberId);

        List<Order> orders = orderRepository.findByMember(member);

        return orders.stream()
                .map(OrderResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto findOrderById(Long orderId) {
        Order order = getOrderInfo(orderId);

        return new OrderResponseDto(order);
    }

    @Override
    public void updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = getOrderInfo(orderId);
        order.updateStatus(status);
    }

    @Override
    public void cancelOrder(Long orderId) {
        Order order = getOrderInfo(orderId);
        order.cancel();
    }

    @Override
    public OrderSaveRequestDto buildSingleOrderDto(MemberDetailsDto member, Long itemOptionId, Integer quantity) {
        if (itemOptionId == null || quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("유효하지 않은 상품 옵션 또는 수량입니다.");
        }

        ItemOption option = itemOptionRepository.findById(itemOptionId)
                .orElseThrow(() -> new IllegalArgumentException("상품 옵션을 찾을 수 없습니다."));

        OrderItemDto dto = OrderItemDto.from(option, quantity);

        return OrderSaveRequestDto.builder()
                .memberId(member.getMemberId())
                .orderDate(LocalDateTime.now())
                .orderItems(Collections.singletonList(dto))
                .customerName(member.getUsername())
                .customerPhone(member.getPhoneNumber())
                .customerAddress(member.getAddress())
                .fromCart(false)
                .build();
    }

    @Override
    public void updateShippingAddress(OrderModificationDto dto) {
        Order order = getOrderInfo(dto.getOrderId());

        if (order.getOrderStatus() == PENDING || order.getOrderStatus() == PROCESSED) {
            order.updateShippingAddress(dto.getNewAddress());
        } else {
            throw new IllegalStateException("배송중이거나 완료된 주문은 주소를 변경할 수 없습니다.");
        }
    }

    @Override
    public void applyRefundOrExchange(OrderModificationDto dto) {
        Order order = getOrderInfo(dto.getOrderId());

        if (order.getOrderStatus() != DELIVERED) {
            throw new IllegalStateException("환불 및 교환 요청은 배송이 완료된 후에 가능합니다.");
        }

        switch (dto.getRequestType()) {
            case REFUND_REQUEST -> order.requestRefund(dto.getReason());
            case EXCHANGE_REQUEST -> order.requestExchange(dto.getReason());
            default -> throw new IllegalArgumentException("잘못된 요청입니다.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponseDto> searchOrders(OrderPageRequestDto requestDto, Pageable pageable) {
        Page<Order> orders = orderRepository.searchOrders(requestDto, pageable);

        return orders.map(OrderResponseDto::new);
    }

    private Order getOrderInfo(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
    }

    private Member getMemberInfo(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다.."));
    }
}