package platform.ecommerce.exception.cart;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class CartNotFoundException extends BusinessException {
    public CartNotFoundException() {
        super(ErrorCode.CART_NOT_FOUND);
    }
}
