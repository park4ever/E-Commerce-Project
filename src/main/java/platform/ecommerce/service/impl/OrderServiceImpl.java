package platform.ecommerce.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import platform.ecommerce.dto.OrderItemDto;
import platform.ecommerce.dto.OrderResponseDto;
import platform.ecommerce.dto.OrderSaveRequestDto;
import platform.ecommerce.entity.*;
import platform.ecommerce.repository.ItemRepository;
import platform.ecommerce.repository.MemberRepository;
import platform.ecommerce.repository.OrderRepository;
import platform.ecommerce.service.CartService;
import platform.ecommerce.service.OrderService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    @Override
    public Long createOrder(OrderSaveRequestDto orderSaveRequestDto) {
        log.info("Creating order with DTO : {}", orderSaveRequestDto);
        Member member = getMemberInfo(orderSaveRequestDto.getMemberId());

        Order order = Order.builder()
                .member(member)
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.PROCESSED)
                .build();

        processOrderItems(order, orderSaveRequestDto.getOrderItems());

        log.info("Order successfully saved to the database : {}", order);
        return orderRepository.save(order).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> findOrdersByMemberId(Long memberId) {
        log.info("Finding orders for member ID : {}", memberId);
        Member member = getMemberInfo(memberId);

        List<Order> orders = orderRepository.findByMember(member);

        return orders.stream()
                .map(OrderResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public void updateOrderStatus(Long orderId, OrderStatus status) {
        log.info("Updating order ID : {} to status : {}", orderId, status);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        order.updateStatus(status);
        orderRepository.save(order);

        log.info("Order status updated successfully.");
    }

    @Override
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        order.cancel();
        orderRepository.save(order);

        log.info("Order [{}] has been cancelled", orderId);
    }

    private Member getMemberInfo(Long memberId) {
        log.info("Retrieving member with ID : {}", memberId);
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다.."));
    }

    private void processOrderItems(Order order, List<OrderItemDto> orderItemDtos) {
        log.info("Processing order items : {}", orderItemDtos);

        for (OrderItemDto dto : orderItemDtos) {
            if (!dto.isValid()) {
                throw new IllegalArgumentException("주문 아이템 정보가 유효하지 않습니다.");
            }
            Item item = itemRepository.findById(dto.getItemId())
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
            if (item.getStockQuantity() < dto.getCount()) {
                log.info("Item ID: {}, Stock Quantity: {}, Order Count: {}", item.getId(), item.getStockQuantity(), dto.getCount());
                throw new IllegalArgumentException("상품의 재고가 부족합니다 : " + item.getItemName());
            }

            OrderItem orderItem = OrderItem.builder()
                    .item(item)
                    .orderPrice(dto.getOrderPrice())
                    .count(dto.getCount())
                    .order(order)
                    .build();

            order.addOrderItem(orderItem);
            item.removeStock(dto.getCount());
        }
        log.info("Order items processed successfully");
    }
}
