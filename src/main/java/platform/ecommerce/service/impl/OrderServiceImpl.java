package platform.ecommerce.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import platform.ecommerce.dto.member.MemberDetailsDto;
import platform.ecommerce.dto.order.*;
import platform.ecommerce.entity.*;
import platform.ecommerce.exception.cart.CartOptionOutOfStockException;
import platform.ecommerce.exception.common.InvalidRequestException;
import platform.ecommerce.exception.coupon.CouponNotFoundException;
import platform.ecommerce.exception.item.ItemOptionNotFoundException;
import platform.ecommerce.exception.member.MemberNotFoundException;
import platform.ecommerce.exception.order.*;
import platform.ecommerce.repository.ItemOptionRepository;
import platform.ecommerce.repository.MemberCouponRepository;
import platform.ecommerce.repository.MemberRepository;
import platform.ecommerce.repository.OrderRepository;
import platform.ecommerce.service.OrderService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
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
            throw new OrderItemListEmptyException();
        }

        Order order = Order.builder()
                .member(getMemberInfo(requestDto.getMemberId()))
                .orderDate(LocalDateTime.now())
                .orderStatus(PROCESSED)
                .shippingAddress(requestDto.convertToShippingAddress())
                .paymentMethod(requestDto.getPaymentMethod())
                .build();

        MemberCoupon coupon = memberCouponRepository.findById(requestDto.getMemberCouponId())
                .orElseThrow(CouponNotFoundException::new);

        order.applyDiscount(coupon, discountAmount);

        for (OrderItemDto dto : requestDto.getOrderItemDto()) {
            ItemOption option = itemOptionRepository.findById(dto.getItemOptionId())
                    .orElseThrow(ItemOptionNotFoundException::new);

            if (option.isSoldOut() || option.getStockQuantity() < dto.getCount()) {
                throw new CartOptionOutOfStockException();
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
            throw new InvalidOrderItemException();
        }

        ItemOption option = itemOptionRepository.findById(itemOptionId)
                .orElseThrow(ItemOptionNotFoundException::new);

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
            throw new AddressChangeNotAllowedException();
        }
    }

    @Override
    public void applyRefundOrExchange(OrderModificationDto dto) {
        Order order = getOrderInfo(dto.getOrderId());

        switch (dto.getRequestType()) {
            case REFUND_REQUEST -> {
                if (order.getOrderStatus() != DELIVERED) {
                    throw new OrderCannotBeRefundedException();
                }
                order.requestRefund(dto.getReason());
            }
            case EXCHANGE_REQUEST -> {
                if (order.getOrderStatus() != DELIVERED) {
                    throw new OrderCannotBeExchangedException();
                }
                order.requestExchange(dto.getReason());
            }
            default -> throw new InvalidRequestException();
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
                .orElseThrow(OrderNotFoundException::new);
    }

    private Member getMemberInfo(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }
}