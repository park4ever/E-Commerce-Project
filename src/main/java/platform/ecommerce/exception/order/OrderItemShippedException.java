package platform.ecommerce.exception.order;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class OrderItemShippedException extends BusinessException {
    public OrderItemShippedException() {
        super(ErrorCode.ORDER_ALREADY_SHIPPED);
    }
}
