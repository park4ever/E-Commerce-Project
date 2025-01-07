package platform.ecommerce.entity;

import lombok.Getter;

@Getter
public enum OrderStatus {

    PENDING,            //주문 대기중
    PROCESSED,          //주문 처리중
    SHIPPED,            //배송중
    DELIVERED,          //배송 완료
    CANCELLED,          //주문 취소
    REFUND_REQUESTED,   //환불 요청됨
    EXCHANGE_REQUESTED, //교환 요청됨
    REFUND_APPROVED,    //환불 승인
    EXCHANGE_APPROVED,  //교환 승인
    COMPLETED           //주문 완료
}
