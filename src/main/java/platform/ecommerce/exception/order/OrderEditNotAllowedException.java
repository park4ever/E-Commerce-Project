package platform.ecommerce.exception.order;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class OrderEditNotAllowedException extends BusinessException {
    public OrderEditNotAllowedException() {
        super(ErrorCode.ORDER_NOT_MODIFIABLE);
    }
}
