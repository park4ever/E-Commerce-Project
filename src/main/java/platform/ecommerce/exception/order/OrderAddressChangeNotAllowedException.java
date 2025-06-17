package platform.ecommerce.exception.order;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class OrderAddressChangeNotAllowedException extends BusinessException {
    public OrderAddressChangeNotAllowedException() {
        super(ErrorCode.ORDER_CANNOT_CHANGE_ADDRESS);
    }
}
