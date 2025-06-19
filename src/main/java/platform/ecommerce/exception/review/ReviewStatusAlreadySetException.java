package platform.ecommerce.exception.review;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class ReviewStatusAlreadySetException extends BusinessException {
    public ReviewStatusAlreadySetException() {
        super(ErrorCode.REVIEW_ALREADY_IN_STATE);
    }
}
