package platform.ecommerce.exception.cart;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class CartAccessDeniedException extends BusinessException {
    public CartAccessDeniedException() {
        super(ErrorCode.CART_ACCESS_DENIED);
    }
}
