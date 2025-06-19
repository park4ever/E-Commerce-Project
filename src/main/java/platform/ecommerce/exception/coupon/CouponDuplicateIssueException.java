package platform.ecommerce.exception.coupon;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class CouponDuplicateIssueException extends BusinessException {
    public CouponDuplicateIssueException() {
        super(ErrorCode.COUPON_ALREADY_ISSUED);
    }
}
