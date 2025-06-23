package platform.ecommerce.exception.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    //COMMON
    INVALID_REQUEST("COMMON_001", "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR("COMMON_999", "서버 오류가 발생하였습니다."),

    //ITEM
    ITEM_NOT_FOUND("ITEM_001", "존재하지 않는 상품입니다."), 
    ITEM_OUT_OF_STOCK("ITEM_002", "상품의 재고가 부족합니다."),
    ITEM_OPTION_REQUIRED("ITEM_003", "최소 하나 이상의 상품 옵션이 필요합니다."),
    ITEM_OPTION_NOT_FOUND("ITEM_004", "상품 옵션을 찾을 수 없습니다."),
    ITEM_OPTION_INVALID("ITEM_005", "상품 옵션이 일치하지 않습니다."),
    ITEM_PRICE_NOT_CHANGED("ITEM_006", "상품의 가격이 기존 가격과 동일합니다."),

    //CART
    CART_QUANTITY_INVALID("CART_001", "수량은 0보다 커야 합니다."),
    CART_ITEM_NOT_FOUND("CART_002", "해당 장바구니 항목이 존재하지 않습니다."), 
    CART_ACCESS_DENIED("CART_003", "해당 장바구니 항목에 대한 권한이 없습니다."),
    CART_ITEM_INVALID("CART_004", "유효하지 않은 상품입니다."),
    CART_OPTION_OUT_OF_STOCK("CART_005", "해당 옵션의 재고가 부족합니다."),
    CART_NOT_FOUND("CART_006", "장바구니를 찾을 수 없습니다. 먼저 장바구니에 상품을 추가하세요."),

    //ORDER
    ORDER_NOT_FOUND("ORDER_001", "주문을 찾을 수 없습니다."),
    ORDER_ALREADY_CANCELLED("ORDER_002", "이미 취소된 주문입니다."), 
    ORDER_STATUS_INVALID("ORDER_003", "주문 상태가 올바르지 않습니다."),
    ORDER_NOT_CANCELABLE("ORDER_004", "배송 준비 중인 경우에만 주문 취소가 가능합니다."),
    ORDER_ADDRESS_NOT_CHANGEABLE("ORDER_005", "배송 준비 중인 경우에만 주소 변경이 가능합니다."),
    ORDER_ALREADY_SHIPPED("ORDER_006", "이미 배송된 상품은 취소할 수 없습니다."),
    ORDER_NOT_MODIFIABLE("ORDER_007", "배송 중이거나 완료된 주문은 수정할 수 없습니다."),
    ORDER_REFUND_NOT_ALLOWED("ORDER_008", "배송 완료 후에만 환불 요청이 가능합니다."),
    ORDER_EXCHANGE_NOT_ALLOWED("ORDER_009", "배송 완료 후에만 교환 요청이 가능합니다."),
    ORDER_EMPTY("ORDER_010", "주문 상품이 비어 있습니다."),
    ORDER_QUANTITY_INVALID("ORDER_011", "주문 수량은 1 이상이어야 합니다."),
    ORDER_PRICE_INVALID("ORDER_012", "가격은 0 이상이어야 합니다."),
    ORDER_STATUS_REQUIRED("ORDER_013", "주문 상태가 비어 있을 수 없습니다."),
    ORDER_ITEM_INVALID("ORDER_014", "유효하지 않은 상품 옵션 또는 수량입니다."),
    ORDER_ITEM_NOT_FOUND("ORDER_015", "주문 상품을 찾을 수 없습니다."),
    //REVIEW
    REVIEW_NOT_FOUND("REVIEW_001", "해당 리뷰를 찾을 수 없습니다."), 
    REVIEW_ACCESS_DENIED("REVIEW_002", "다른 회원이 작성한 리뷰는 수정할 수 없습니다."),
    REVIEW_REPLY_NOT_FOUND("REVIEW_003", "관리자 답변이 없습니다."),
    REVIEW_REPLY_EMPTY("REVIEW_004", "관리자 답변은 비어 있을 수 없습니다."),
    REVIEW_ALREADY_IN_STATE("REVIEW_005", "이미 해당 상태로 설정되어 있습니다."),

    //COUPON
    COUPON_NOT_FOUND("COUPON_001", "존재하지 않는 쿠폰입니다."),
    COUPON_ALREADY_USED("COUPON_002", "이미 사용된 쿠폰입니다."),
    COUPON_ALREADY_ISSUED("COUPON_003", "이미 발급된 쿠폰입니다."),
    COUPON_INVALID("COUPON_004", "유효하지 않는 쿠폰입니다."),
    COUPON_NOT_USABLE("COUPON_005", "사용할 수 없는 쿠폰입니다."),

    //FILE
    FILE_UPLOAD_FAILED("FILE_001", "파일 업로드에 실패하였습니다."), 
    FILE_DELETE_FAILED("FILE_002", "파일 삭제에 실패하였습니다."), 

    //MEMBER
    MEMBER_NOT_FOUND("MEMBER_001", "회원을 찾을 수 없습니다."),
    MEMBER_PASSWORD_MISMATCH("MEMBER_002", "새 비밀번호가 일치하지 않습니다."),
    MEMBER_PASSWORD_REQUIRED("MEMBER_003", "비밀번호를 입력해주세요."),
    MEMBER_ACCESS_DENIED("MEMBER_004", "로그인이 필요한 요청입니다."),

    //UNSUPPORTED
    NOT_IMPLEMENTED("SYSTEM_001", "추후 구현 예정입니다."); 

    private final String code;
    private final String message;
}