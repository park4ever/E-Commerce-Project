package platform.ecommerce.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import platform.ecommerce.dto.member.MemberDetailsDto;
import platform.ecommerce.dto.order.*;
import platform.ecommerce.entity.*;
import platform.ecommerce.repository.ItemRepository;
import platform.ecommerce.repository.MemberRepository;
import platform.ecommerce.repository.OrderRepository;
import platform.ecommerce.service.OrderService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static platform.ecommerce.dto.order.OrderModificationDto.RequestType.*;
import static platform.ecommerce.entity.OrderStatus.*;

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

        //주문 준비
        Order order = prepareOrder(orderSaveRequestDto);

        //주문 항목 처리
        processOrderItems(order, orderSaveRequestDto.getOrderItemDto());

        //주문 저장
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
    @Transactional(readOnly = true)
    public OrderResponseDto findOrderById(Long orderId) {
        log.info("Finding order by ID : {}", orderId);
        Order order = getOrderInfo(orderId);

        return new OrderResponseDto(order);
    }

    @Override
    public void updateOrderStatus(Long orderId, OrderStatus status) {
        log.info("Updating order ID : {} to status : {}", orderId, status);
        Order order = getOrderInfo(orderId);

        order.updateStatus(status);
        orderRepository.save(order);

        log.info("Order status updated successfully.");
    }

    @Override
    public void cancelOrder(Long orderId) {
        Order order = getOrderInfo(orderId);

        order.cancel();
        orderRepository.save(order);

        log.info("Order [{}] has been cancelled", orderId);
    }

    @Override
    public OrderSaveRequestDto createOrderSaveRequestDto(MemberDetailsDto member, Long itemId, Integer quantity) {
        OrderSaveRequestDto orderSaveRequestDto = OrderSaveRequestDto.builder()
                .orderDate(LocalDateTime.now())
                .orderItems(new ArrayList<>())
                .customerName(member.getUsername())
                .customerPhone(member.getPhoneNumber())
                .customerAddress(member.getFullAddress())
                .customerAdditionalInfo(member.getAdditionalInfo())
                .quantity(quantity)
                .build();

        if (itemId != null) {
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
            OrderItemDto orderItemDto = OrderItemDto.builder()
                    .itemId(itemId)
                    .itemName(item.getItemName())
                    .orderPrice(item.getPrice())
                    .count(quantity)
                    .build();
            log.info("orderItemDto : {}", orderItemDto);
                orderSaveRequestDto.getOrderItemDto().add(orderItemDto);
                log.info("orderSaveRequestDto : {}", orderSaveRequestDto);
        }

        return orderSaveRequestDto;
    }

    @Override
    public void updateShippingAddress(OrderModificationDto dto) {
        log.info("Updating shipping address for order ID : {}", dto.getOrderId());
        Order order = getOrderInfo(dto.getOrderId());

        //배송 전 단계에서만 주소 변경 가능
        if (order.getOrderStatus() == PENDING || order.getOrderStatus() == PROCESSED) {
            Address newAddress = dto.getNewAddress();
            orderRepository.save(order);

            log.info("Current Address : {}", order.getShippingAddress());
            log.info("New Address : {}", newAddress);

            order.updateShippingAddress(newAddress);
            orderRepository.save(order);

            log.info("Shipping address updated successfully.");
        } else {
            throw new IllegalStateException("배송중이거나 완료된 주문은 주소를 변경할 수 없습니다.");
        }
    }

    @Override
    public void requestRefundOrExchange(OrderModificationDto dto) {
        log.info("Requesting {} for order ID : {}", dto.getRequestType(), dto.getOrderId());
        Order order = getOrderInfo(dto.getOrderId());

        if (order.getOrderStatus() != DELIVERED) {
            throw new IllegalStateException("환불 및 교환 요청은 배송이 완료된 후에 가능합니다.");
        }

        //환불 및 교환 요청 처리
        switch (dto.getRequestType()) {
            case REFUND_REQUEST -> order.requestRefund(dto.getReason());
            case EXCHANGE_REQUEST -> order.requestExchange(dto.getReason());
            default -> throw new IllegalArgumentException("잘못된 요청입니다.");
        }

        orderRepository.save(order);
        log.info("{} requested successfully.", dto.getRequestType());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponseDto> findOrdersWithPageable(OrderSearchCondition cond, Long memberId, Pageable pageable) {
        Sort sort = getSort(cond);
        Pageable pageableWithSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        setDateByCondition(cond);
        Page<Order> orders = orderRepository.findOrdersWithFilters(cond, memberId, pageableWithSort);

        return orders.map(order -> {
            OrderResponseDto dto = new OrderResponseDto(order);
            log.info("OrderResponseDto created : {}", dto);
            return dto;
        });

//        return orders.map(OrderResponseDto::new);
    }

    private Sort getSort(OrderSearchCondition cond) {
        if ("orderDate".equalsIgnoreCase(cond.getSortBy())) {
            return cond.isAscending() ? Sort.by("orderDate").ascending() : Sort.by("orderDate").descending();
        } else if ("status".equalsIgnoreCase(cond.getSortBy())) {
            return cond.isAscending() ? Sort.by("orderStatus").ascending() : Sort.by("orderStatus").descending();
        }
        return Sort.by("orderId").ascending(); //기본 정렬 옵션
    }

    private static void setDateByCondition(OrderSearchCondition cond) {
        LocalDate startDate = cond.getStartDate();
        LocalDate endDate = cond.getEndDate();

        //시작 날짜에 시간이 없다면 00:00:00으로 설정
        if (startDate != null) {
            cond.setStartDate(startDate.atStartOfDay().toLocalDate());
        }

        //종료 날짜에 시간이 없다면 23:59:59으로 설정
        if (endDate != null) {
            cond.setEndDate(endDate.atTime(LocalTime.MAX).toLocalDate());
        }

        //수정된 날짜 범위를 다시 검색 조건에 설정
        cond.setStartDate(startDate);
        cond.setEndDate(endDate);
    }

    private Order prepareOrder(OrderSaveRequestDto orderSaveRequestDto) {
        log.info("Preparing order for member ID : {}", orderSaveRequestDto.getMemberId());

        //회원 정보 가져오기
        Member member = getMemberInfo(orderSaveRequestDto.getMemberId());

        //주문 엔티티 생성
        return Order.builder()
                .member(member)
                .orderDate(LocalDateTime.now())
                .orderStatus(PROCESSED)
                .shippingAddress(orderSaveRequestDto.convertToShippingAddress())
                .paymentMethod(orderSaveRequestDto.getPaymentMethod())
                .build();
    }

    private void processOrderItems(Order order, List<OrderItemDto> orderItemDtos) {
        log.info("Processing order items : {}", orderItemDtos);

        orderItemDtos.forEach(dto -> {
            if (!dto.isValid()) {
                throw new IllegalArgumentException("주문 아이템 정보가 유효하지 않습니다.");
            }

            Item item = itemRepository.findById(dto.getItemId())
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

            //재고 확인 및 처리
            if (item.isOutOfStock(dto.getCount())) {
                log.warn("재고 부족 - Item ID : {}, Stock : {}, Requested : {}", item.getId(), item.getStockQuantity(), dto.getCount());
                throw new IllegalArgumentException("상품의 재고가 부족합니다 : " + item.getItemName());
            }

            //주문 아이템 생성 및 주문에 추가
            OrderItem orderItem = dto.toOrderItem(item, order);
            order.addOrderItem(orderItem);
            //재고 차감
            item.removeStock(dto.getCount());
        });
        log.info("Order items processed successfully");
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
