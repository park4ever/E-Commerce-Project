package platform.ecommerce.exception.order;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class OrderAccessDeniedException extends BusinessException {
    public OrderAccessDeniedException() {
        super(ErrorCode.ORDER_ACCESS_DENIED);
    }
}
