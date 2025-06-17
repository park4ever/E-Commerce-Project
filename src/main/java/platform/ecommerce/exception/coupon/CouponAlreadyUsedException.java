package platform.ecommerce.exception.coupon;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class CouponAlreadyUsedException extends BusinessException {
    public CouponAlreadyUsedException() {
        super(ErrorCode.COUPON_ALREADY_USED);
    }
}
