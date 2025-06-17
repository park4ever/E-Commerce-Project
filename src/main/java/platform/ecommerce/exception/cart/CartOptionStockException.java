package platform.ecommerce.exception.cart;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class CartOptionStockException extends BusinessException {
    public CartOptionStockException() {
        super(ErrorCode.CART_INVALID_OPTION);
    }
}
