package platform.ecommerce.exception.item;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class NotEnoughStockException extends BusinessException {
    public NotEnoughStockException() {
        super(ErrorCode.NOT_ENOUGH_STOCK);
    }
}
