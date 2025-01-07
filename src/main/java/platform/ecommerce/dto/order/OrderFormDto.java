package platform.ecommerce.dto.order;

import lombok.Data;
import platform.ecommerce.entity.PaymentMethod;

@Data
public class OrderFormDto {

    private Long itemId;
    private Integer quantity = 1;
    private String shippingAddress;
    private PaymentMethod paymentMethod;
}
