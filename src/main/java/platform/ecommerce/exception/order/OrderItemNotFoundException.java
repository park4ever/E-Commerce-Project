package platform.ecommerce.exception.order;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class OrderItemNotFoundException extends BusinessException {
    public OrderItemNotFoundException() {
        super(ErrorCode.ORDER_ITEM_NOT_FOUND);
    }
}
