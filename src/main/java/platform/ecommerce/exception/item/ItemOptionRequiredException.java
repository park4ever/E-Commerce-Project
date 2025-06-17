package platform.ecommerce.exception.item;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class ItemOptionRequiredException extends BusinessException {
    public ItemOptionRequiredException() {
        super(ErrorCode.ITEM_OPTION_REQUIRED);
    }
}
