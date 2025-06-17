package platform.ecommerce.exception.coupon;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class InvalidCouponException extends BusinessException {
    public InvalidCouponException() {
        super(ErrorCode.COUPON_INVALID);
    }
}
