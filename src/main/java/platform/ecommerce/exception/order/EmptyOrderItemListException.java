package platform.ecommerce.exception.order;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class EmptyOrderItemListException extends BusinessException {
    public EmptyOrderItemListException() {
        super(ErrorCode.ORDER_EMPTY);
    }
}
