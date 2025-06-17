package platform.ecommerce.exception.member;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class MemberPasswordMismatchException extends BusinessException {
    public MemberPasswordMismatchException() {
        super(ErrorCode.MEMBER_PASSWORD_MISMATCH);
    }
}
