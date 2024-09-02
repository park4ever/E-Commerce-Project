package platform.ecommerce.dto;

import lombok.Getter;
import platform.ecommerce.entity.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class OrderResponseDto {

    private Long orderId;
    private LocalDateTime orderDate;
    private String orderStatus;
    private List<OrderItemDto> orderItems;

    public OrderResponseDto(Order order) {
        this.orderId = order.getId();
        this.orderDate = order.getOrderDate();
        this.orderStatus = order.getOrderStatus().toString();
        this.orderItems = order.getOrderItems().stream()
                .map(OrderItemDto::new)
                .collect(Collectors.toList());
    }
}
