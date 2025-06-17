package platform.ecommerce.exception.item;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class ItemNotFoundException extends BusinessException {
    public ItemNotFoundException() {
        super(ErrorCode.CART_ITEM_NOT_FOUND);
    }
}
