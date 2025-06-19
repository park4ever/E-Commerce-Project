package platform.ecommerce.exception.order;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class OrderQuantityInvalidException extends BusinessException {
    public OrderQuantityInvalidException() {
        super(ErrorCode.ORDER_QUANTITY_INVALID);
    }
}
