package platform.ecommerce.exception.order;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class OrderCannotBeExchangedException extends BusinessException {
    public OrderCannotBeExchangedException() {
        super(ErrorCode.ORDER_EXCHANGE_NOT_ALLOWED);
    }
}
