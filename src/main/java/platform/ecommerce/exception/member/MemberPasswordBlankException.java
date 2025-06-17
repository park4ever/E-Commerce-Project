package platform.ecommerce.exception.member;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class MemberPasswordBlankException extends BusinessException {
    public MemberPasswordBlankException() {
        super(ErrorCode.MEMBER_PASSWORD_BLANK);
    }
}
