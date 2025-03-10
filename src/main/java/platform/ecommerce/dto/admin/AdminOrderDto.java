package platform.ecommerce.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import platform.ecommerce.entity.OrderStatus;
import platform.ecommerce.entity.PaymentMethod;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminOrderDto {
    
    private Long id;
    private String memberEmail;
    private String memberName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderDate;

    private OrderStatus orderStatus;
    private PaymentMethod paymentMethod;
    private String shippingAddress;
    private LocalDateTime lastModifiedDate;

    private List<AdminOrderItemDto> orderItems;
    private int totalAmount;

    private boolean isPaid;
    private boolean isCancelable;
}
