package platform.ecommerce.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import platform.ecommerce.entity.OrderStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderUpdateRequest {

    private OrderStatus orderStatus;
    private String shippingAddress;
    private String modificationReason;
}
