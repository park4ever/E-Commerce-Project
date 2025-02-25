package platform.ecommerce.dto.order;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import platform.ecommerce.entity.Item;
import platform.ecommerce.entity.Order;
import platform.ecommerce.entity.OrderItem;

@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto {

    private Long itemId;
    private String itemName;
    private Integer orderPrice; // 주문 시 가격
    private Integer count;      // 주문할 수량
    private String imageUrl; // 이미지 경로
    private Integer totalPrice;

    public OrderItemDto(OrderItem orderItem) {
        this.itemId = orderItem.getItem().getId();
        this.itemName = orderItem.getItem().getItemName();
        this.orderPrice = orderItem.getOrderPrice();
        this.count = orderItem.getCount();
        this.imageUrl = orderItem.getItem().getImageUrl();
        this.totalPrice = this.orderPrice * this.count;
    }

    public boolean isValid() {
        log.info("itemId = {} orderPrice = {} count = {}", itemId, orderPrice, count);
        return itemId != null && orderPrice != null && orderPrice > 0 && count != null && count > 0;
    }

    public OrderItem toOrderItem(Item item, Order order) {
        return OrderItem.builder()
                .item(item)
                .orderPrice(this.getOrderPrice())
                .count(this.getCount())
                .imageUrl(item.getImageUrl())
                .order(order)
                .build();
    }
}
