package platform.ecommerce.exception.cart;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class InvalidCartItemException extends BusinessException {
    public InvalidCartItemException() {
        super(ErrorCode.CART_ITEM_INVALID);
    }
}
