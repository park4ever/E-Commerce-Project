package platform.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import platform.ecommerce.entity.OrderItem;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto {

    private Long itemId;
    private String itemName;
    private Integer orderPrice; // 주문 시 가격
    private Integer count;      // 주문할 수량

    public OrderItemDto(OrderItem orderItem) {
        this.itemId = orderItem.getItem().getId();
        this.itemName = orderItem.getItem().getItemName();
        this.orderPrice = orderItem.getOrderPrice();
        this.count = orderItem.getCount();
    }

    public boolean isValid() {
        log.info("itemId = {} orderPrice = {} count = {}", itemId, orderPrice, count);
        return itemId != null && orderPrice != null && count != null && count > 0;
    }
}
