package platform.ecommerce.exception.order;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class OrderItemListEmptyException extends BusinessException {
    public OrderItemListEmptyException() {
        super(ErrorCode.ORDER_EMPTY);
    }
}
