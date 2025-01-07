package platform.ecommerce.dto.order;

import lombok.*;
import platform.ecommerce.entity.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderSaveRequestDto {

    private Long memberId;
    private LocalDateTime orderDate;
    private List<OrderItemDto> orderItems = new ArrayList<>();

    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private Address shippingAddress;
    private PaymentMethod paymentMethod;

    private Integer quantity;

    public List<OrderItemDto> getOrderItemDto() {
        return orderItems;
    }

    //변환 메서드
    public Address convertToCustomerAddress() {
        return Address.fromFullAddress(this.customerAddress);
    }

    public Address convertToShippingAddress() {
        return this.shippingAddress;
    }
}
