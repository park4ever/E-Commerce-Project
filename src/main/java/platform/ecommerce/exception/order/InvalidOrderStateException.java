package platform.ecommerce.exception.order;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class InvalidOrderStateException extends BusinessException {
    public InvalidOrderStateException() {
        super(ErrorCode.ORDER_INVALID_STATE);
    }
}
