package platform.ecommerce.exception.item;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class ItemPriceUnchangedException extends BusinessException {
    public ItemPriceUnchangedException() {
        super(ErrorCode.ITEM_PRICE_NOT_CHANGED);
    }
}
