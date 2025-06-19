package platform.ecommerce.exception.order;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class OrderStatusInvalidException extends BusinessException {
    public OrderStatusInvalidException() {
        super(ErrorCode.ORDER_STATUS_INVALID);
    }
}
