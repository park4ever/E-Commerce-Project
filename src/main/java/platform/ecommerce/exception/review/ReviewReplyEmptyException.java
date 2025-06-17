package platform.ecommerce.exception.review;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class ReviewReplyEmptyException extends BusinessException {
    public ReviewReplyEmptyException() {
        super(ErrorCode.REVIEW_REPLY_EMPTY);
    }
}
