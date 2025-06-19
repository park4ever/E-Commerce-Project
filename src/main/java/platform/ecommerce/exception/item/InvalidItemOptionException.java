package platform.ecommerce.exception.item;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class InvalidItemOptionException extends BusinessException {
    public InvalidItemOptionException() {
        super(ErrorCode.ITEM_OPTION_INVALID);
    }
}
