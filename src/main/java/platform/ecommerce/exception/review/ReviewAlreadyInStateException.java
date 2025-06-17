package platform.ecommerce.exception.review;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class ReviewAlreadyInStateException extends BusinessException {
    public ReviewAlreadyInStateException() {
        super(ErrorCode.REVIEW_ALREADY_IN_STATE);
    }
}
