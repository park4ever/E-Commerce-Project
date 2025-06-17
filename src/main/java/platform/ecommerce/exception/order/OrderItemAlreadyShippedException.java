package platform.ecommerce.exception.order;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class OrderItemAlreadyShippedException extends BusinessException {
    public OrderItemAlreadyShippedException() {
        super(ErrorCode.ORDER_ALREADY_SHIPPED);
    }
}
