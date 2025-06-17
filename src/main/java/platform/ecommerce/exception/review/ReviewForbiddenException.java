package platform.ecommerce.exception.review;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class ReviewForbiddenException extends BusinessException {
    public ReviewForbiddenException() {
        super(ErrorCode.REVIEW_FORBIDDEN);
    }
}
