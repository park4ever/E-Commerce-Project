package platform.ecommerce.exception.order;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class OrderPriceInvalidException extends BusinessException {
    public OrderPriceInvalidException() {
        super(ErrorCode.ORDER_PRICE_INVALID);
    }
}
