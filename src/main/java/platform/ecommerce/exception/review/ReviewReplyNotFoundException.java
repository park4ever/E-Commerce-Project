package platform.ecommerce.exception.review;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class ReviewReplyNotFoundException extends BusinessException {
    public ReviewReplyNotFoundException() {
        super(ErrorCode.REVIEW_REPLY_NOT_FOUND);
    }
}
