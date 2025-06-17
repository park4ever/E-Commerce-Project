package platform.ecommerce.exception.order;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class InvalidOrderQuantityException extends BusinessException {
    public InvalidOrderQuantityException() {
        super(ErrorCode.ORDER_INVALID_QUANTITY);
    }
}
