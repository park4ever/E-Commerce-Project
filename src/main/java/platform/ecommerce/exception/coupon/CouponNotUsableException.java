package platform.ecommerce.exception.coupon;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class CouponNotUsableException extends BusinessException {
    public CouponNotUsableException() {
        super(ErrorCode.COUPON_NOT_USABLE);
    }
}
