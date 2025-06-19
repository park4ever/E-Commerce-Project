package platform.ecommerce.exception.order;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class OrderStatusRequiredException extends BusinessException {
    public OrderStatusRequiredException() {
        super(ErrorCode.ORDER_STATUS_REQUIRED);
    }
}
