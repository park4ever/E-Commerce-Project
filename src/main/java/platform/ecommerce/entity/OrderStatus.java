package platform.ecommerce.entity;

import lombok.Getter;

@Getter
public enum OrderStatus {

    PENDING,        //주문 대기중
    PROCESSED,      //주문 처리중
    SHIPPED,        //배송중
    DELIVERED,      //배송 완료
    CANCELLED       //주문 취소
}
