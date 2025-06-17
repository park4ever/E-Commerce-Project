package platform.ecommerce.exception.order;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class OrderCancelNotAllowedException extends BusinessException {
    public OrderCancelNotAllowedException() {
        super(ErrorCode.ORDER_CANNOT_CANCEL);
    }
}
