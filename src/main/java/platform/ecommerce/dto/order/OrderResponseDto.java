package platform.ecommerce.dto.order;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import platform.ecommerce.entity.Address;
import platform.ecommerce.entity.Order;
import platform.ecommerce.entity.OrderItem;
import platform.ecommerce.entity.PaymentMethod;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Getter
public class OrderResponseDto {

    private Long orderId;
    private LocalDateTime orderDate;
    private String orderStatus;
    private List<OrderItemDto> orderItems;
    private Address shippingAddress;
    private PaymentMethod paymentMethod;

    public OrderResponseDto(Order order) {
        this.orderId = order.getId();
        this.orderDate = order.getOrderDate();
        this.orderStatus = order.getOrderStatus().toString();

        log.info("Creating OrderResponseDto for order ID : {}", orderId);

        this.orderItems = order.getOrderItems().stream()
                .map(orderItem -> {
                    OrderItemDto dto = new OrderItemDto(orderItem);
                    log.info("OrderItemDto created : {}", dto);
                    return dto;
                })
                .collect(Collectors.toList());

        /*this.orderItems = order.getOrderItems().stream()
                .map(OrderItemDto::new)
                .collect(Collectors.toList());*/

        this.shippingAddress = order.getShippingAddress();
        this.paymentMethod = order.getPaymentMethod();
    }
}
