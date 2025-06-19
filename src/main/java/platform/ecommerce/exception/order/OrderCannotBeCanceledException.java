package platform.ecommerce.exception.order;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class OrderCannotBeCanceledException extends BusinessException {
    public OrderCannotBeCanceledException() {
        super(ErrorCode.ORDER_NOT_CANCELABLE);
    }
}
