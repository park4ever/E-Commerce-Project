package platform.ecommerce.exception.order;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class OrderCannotBeRefundedException extends BusinessException {
    public OrderCannotBeRefundedException() {
        super(ErrorCode.ORDER_REFUND_NOT_ALLOWED);
    }
}
