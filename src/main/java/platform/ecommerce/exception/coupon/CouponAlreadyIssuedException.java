package platform.ecommerce.exception.coupon;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class CouponAlreadyIssuedException extends BusinessException {
    public CouponAlreadyIssuedException() {
        super(ErrorCode.COUPON_ALREADY_ISSUED);
    }
}
