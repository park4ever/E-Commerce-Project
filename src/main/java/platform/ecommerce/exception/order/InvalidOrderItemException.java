package platform.ecommerce.exception.order;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class InvalidOrderItemException extends BusinessException {
    public InvalidOrderItemException() {
        super(ErrorCode.ORDER_ITEM_INVALID);
    }
}
