package platform.ecommerce.exception.cart;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class InvalidCartQuantityException extends BusinessException {
    public InvalidCartQuantityException() {
        super(ErrorCode.CART_INVALID_QUANTITY);
    }
}
