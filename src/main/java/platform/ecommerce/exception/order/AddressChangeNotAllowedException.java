package platform.ecommerce.exception.order;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class AddressChangeNotAllowedException extends BusinessException {
    public AddressChangeNotAllowedException() {
        super(ErrorCode.ORDER_ADDRESS_NOT_CHANGEABLE);
    }
}
