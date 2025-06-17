package platform.ecommerce.exception.order;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class OrderModificationNotAllowedException extends BusinessException {
    public OrderModificationNotAllowedException() {
        super(ErrorCode.ORDER_CANNOT_MODIFY);
    }
}
