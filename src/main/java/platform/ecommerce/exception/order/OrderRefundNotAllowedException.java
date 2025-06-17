package platform.ecommerce.exception.order;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class OrderRefundNotAllowedException extends BusinessException {
    public OrderRefundNotAllowedException() {
        super(ErrorCode.ORDER_REFUND_NOT_ALLOWED);
    }
}
