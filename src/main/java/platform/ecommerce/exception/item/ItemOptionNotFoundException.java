package platform.ecommerce.exception.item;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class ItemOptionNotFoundException extends BusinessException {
    public ItemOptionNotFoundException() {
        super(ErrorCode.ITEM_OPTION_NOT_FOUND);
    }
}
