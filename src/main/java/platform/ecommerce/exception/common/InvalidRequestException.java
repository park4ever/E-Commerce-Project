package platform.ecommerce.exception.common;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class InvalidRequestException extends BusinessException {
    public InvalidRequestException() {
        super(ErrorCode.INVALID_REQUEST);
    }
}
