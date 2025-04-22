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

@Getter
public class OrderResponseDto {

    private Long orderId;
    private LocalDateTime orderDate;
    private String orderStatus;
    private List<OrderItemDto> orderItems;
    private Address shippingAddress;
    private PaymentMethod paymentMethod;

    public OrderResponseDto(Order order) {
        orderId = order.getId();
        orderDate = order.getOrderDate();
        orderStatus = order.getOrderStatus().toString();

        orderItems = order.getOrderItems().stream()
                .map(OrderItemDto::new)
                .collect(Collectors.toList());

        shippingAddress = order.getShippingAddress();
        paymentMethod = order.getPaymentMethod();
    }
}
