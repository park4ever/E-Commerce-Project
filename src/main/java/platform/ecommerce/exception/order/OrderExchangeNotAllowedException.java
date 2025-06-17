package platform.ecommerce.exception.order;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class OrderExchangeNotAllowedException extends BusinessException {
    public OrderExchangeNotAllowedException() {
        super(ErrorCode.ORDER_EXCHANGE_NOT_ALLOWED);
    }
}
