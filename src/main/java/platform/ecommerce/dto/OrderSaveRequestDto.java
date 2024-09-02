package platform.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import platform.ecommerce.entity.Member;
import platform.ecommerce.entity.OrderItem;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSaveRequestDto {

    private Long memberId;
    private LocalDateTime orderDate;
    private List<OrderItemDto> orderItems;
}
