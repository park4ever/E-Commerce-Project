package platform.ecommerce.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import platform.ecommerce.entity.Address;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderModificationDto {

    private Long orderId;
    private String reason;
    private Address newAddress;
    private RequestType requestType;

    public enum RequestType {
        ADDRESS_CHANGE,     //배송지변경
        REFUND_REQUEST,     //환불요청
        EXCHANGE_REQUEST    //교환요청
    }
}

