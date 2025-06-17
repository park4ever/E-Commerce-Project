package platform.ecommerce.exception.unsupported;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class NotImplementedException extends BusinessException {
    public NotImplementedException() {
        super(ErrorCode.NOT_IMPLEMENTED);
    }
}
