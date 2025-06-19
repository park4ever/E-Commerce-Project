package platform.ecommerce.exception.cart;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class CartOptionOutOfStockException extends BusinessException {
    public CartOptionOutOfStockException() {
        super(ErrorCode.CART_OPTION_OUT_OF_STOCK);
    }
}
