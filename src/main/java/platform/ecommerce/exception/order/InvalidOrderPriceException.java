package platform.ecommerce.exception.order;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class InvalidOrderPriceException extends BusinessException {
    public InvalidOrderPriceException() {
        super(ErrorCode.ORDER_INVALID_PRICE);
    }
}
