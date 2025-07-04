package platform.ecommerce.exception.item;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class NotEnoughStockException extends BusinessException {
    public NotEnoughStockException() {
        super(ErrorCode.ITEM_OUT_OF_STOCK);
    }
}
