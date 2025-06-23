package platform.ecommerce.exception.member;

import platform.ecommerce.exception.BusinessException;
import platform.ecommerce.exception.error.ErrorCode;

public class MemberAccessDeniedException extends BusinessException {
    public MemberAccessDeniedException() {
        super(ErrorCode.MEMBER_ACCESS_DENIED);
    }
}
