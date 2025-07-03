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
import platform.ecommerce.exception.coupon.CouponNotUsableException;
import platform.ecommerce.exception.item.ItemOptionNotFoundException;
import platform.ecommerce.exception.member.MemberNotFoundException;
import platform.ecommerce.exception.order.*;
import platform.ecommerce.repository.ItemOptionRepository;
import platform.ecommerce.repository.MemberCouponRepository;
import platform.ecommerce.repository.MemberRepository;
import platform.ecommerce.repository.OrderRepository;
import platform.ecommerce.service.CartService;
import platform.ecommerce.service.MemberCouponService;
import platform.ecommerce.service.OrderService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static platform.ecommerce.entity.OrderStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemOptionRepository itemOptionRepository;
    private final MemberCouponRepository memberCouponRepository;

    private final CartService cartService;  //단방향 주입이라 순환참조 X.
    private final MemberCouponService memberCouponService;  //위와 동일.

    @Override
    public Long placeOrder(OrderSaveRequestDto dto) {
        if (dto == null || dto.getOrderItemDto() == null || dto.getOrderItemDto().isEmpty()) {
            throw new OrderItemListEmptyException();
        }

        //주문 엔티티 생성
        Order order = Order.builder()
                .member(findMemberByIdOrThrow(dto.getMemberId()))
                .orderDate(LocalDateTime.now())
                .orderStatus(PROCESSED)
                .shippingAddress(dto.convertToShippingAddress())
                .paymentMethod(dto.getPaymentMethod())
                .build();

        //주문 상품 생성 및 재고 차감
        for (OrderItemDto itemDto : dto.getOrderItemDto()) {
            ItemOption option = itemOptionRepository.findById(itemDto.getItemOptionId())
                    .orElseThrow(ItemOptionNotFoundException::new);

            if (option.isSoldOut() || option.getStockQuantity() < itemDto.getCount()) {
                throw new CartOptionOutOfStockException();
            }

            order.addOrderItem(itemDto.toOrderItem(option, order));
            option.removeStock(itemDto.getCount());
        }

        //할인 적용 (옵션)
        if (dto.getMemberCouponId() != null) {
            MemberCoupon coupon = memberCouponRepository.findById(dto.getMemberCouponId())
                    .orElseThrow(CouponNotFoundException::new);

            int orderTotal = order.getTotalPrice();
            if (!coupon.isUsable(orderTotal)) {
                throw new CouponNotUsableException();
            }

            order.applyDiscount(coupon, coupon.getDiscountAmount(orderTotal));
            coupon.markAsUsed();
        }

        return orderRepository.save(order).getId();
    }

    @Override
    public Long processOrder(OrderSaveRequestDto dto, Long memberId) {
        //장바구니에서 주문한 경우 DTO 가공
        if (dto.isFromCart()) {
            dto = cartService.prepareOrderFromCart(memberId);
        } else {
            if (dto.getMemberId() == null) {
                dto.setMemberId(memberId);
            }
        }

        //쿠폰 사용 가능 여부 검증 및 할인 정보 반영
        if (dto.getMemberCouponId() != null) {
            MemberCoupon coupon = memberCouponService.getOwnedCouponOrThrow(dto.getMemberCouponId(), memberId);

            int orderTotal = dto.getOrderItemDto().stream()
                    .mapToInt(OrderItemDto::getTotalPrice)
                    .sum();

            if (!coupon.isUsable(orderTotal)) {
                throw new CouponNotUsableException();
            }

            //쿠폰 사용 처리 (서비스에 위임)
            memberCouponService.useCoupon(dto.getMemberCouponId(), memberId);
        }

        //주문 처리
        Long orderId = placeOrder(dto);

        //장바구니 주문이라면 해당 상품 삭제
        if (dto.isFromCart()) {
            List<Long> orderedItemIds = dto.getOrderItems().stream()
                    .map(OrderItemDto::getItemOptionId)
                    .toList();

            cartService.removeOrderedItemsFromCart(memberId, orderedItemIds);
        }

        return orderId;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponseDto> findMyOrders(Long memberId, OrderPageRequestDto dto) {
        Pageable pageable = dto.toPageable();
        Page<Order> orders = orderRepository.searchMyOrders(memberId, dto, pageable);

        return orders.map(OrderResponseDto::new);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto findOrderById(Long orderId, Long memberId) {
        Order order = findOrderByIdOrThrow(orderId);

        validateOwnership(memberId, order);

        return new OrderResponseDto(order);
    }

    @Override
    public void cancelOrder(Long orderId, Long memberId) {
        Order order = findOrderByIdOrThrow(orderId);

        validateOwnership(memberId, order);

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
    public void updateShippingAddress(OrderModificationDto dto, Long memberId) {
        if (dto.getRequestType() != OrderModificationDto.RequestType.ADDRESS_CHANGE) {
            throw new InvalidRequestException();
        }

        Order order = findOrderByIdOrThrow(dto.getOrderId());

        validateOwnership(memberId, order);

        order.updateShippingAddress(dto.getNewAddress());
    }

    @Override
    public void applyRefundOrExchange(OrderModificationDto dto, Long memberId) {
        Order order = findOrderByIdOrThrow(dto.getOrderId());

        validateOwnership(memberId, order);

        switch (dto.getRequestType()) {
            case REFUND_REQUEST -> order.requestRefund(dto.getReason());
            case EXCHANGE_REQUEST -> order.requestExchange(dto.getReason());
            default -> throw new InvalidRequestException();
        }
    }

    private static void validateOwnership(Long memberId, Order order) {
        if (!order.getMember().getId().equals(memberId)) {
            throw new OrderAccessDeniedException();
        }
    }

    private Order findOrderByIdOrThrow(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);
    }

    private Member findMemberByIdOrThrow(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }
}