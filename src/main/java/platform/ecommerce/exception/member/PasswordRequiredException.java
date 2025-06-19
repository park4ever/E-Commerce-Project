package platform.ecommerce.exception.member;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class PasswordRequiredException extends BusinessException {
    public PasswordRequiredException() {
        super(ErrorCode.MEMBER_PASSWORD_REQUIRED);
    }
}
