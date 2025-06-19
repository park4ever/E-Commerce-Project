package platform.ecommerce.exception.unsupported;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class FeatureNotAvailableException extends BusinessException {
    public FeatureNotAvailableException() {
        super(ErrorCode.NOT_IMPLEMENTED);
    }
}
