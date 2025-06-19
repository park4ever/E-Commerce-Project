package platform.ecommerce.exception.order;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class OrderCanceledException extends BusinessException {
    public OrderCanceledException() {
        super(ErrorCode.ORDER_ALREADY_CANCELLED);
    }
}
