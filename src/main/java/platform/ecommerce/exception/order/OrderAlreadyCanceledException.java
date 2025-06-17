package platform.ecommerce.exception.order;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class OrderAlreadyCanceledException extends BusinessException {
    public OrderAlreadyCanceledException() {
        super(ErrorCode.ORDER_ALREADY_CANCELLED);
    }
}
