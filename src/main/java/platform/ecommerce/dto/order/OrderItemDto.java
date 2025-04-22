package platform.ecommerce.dto.order;

import lombok.*;
import platform.ecommerce.entity.Item;
import platform.ecommerce.entity.ItemOption;
import platform.ecommerce.entity.Order;
import platform.ecommerce.entity.OrderItem;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto {

    private Long itemOptionId;
    private String itemName;
    private Integer orderPrice;     // 주문 시 가격
    private Integer count;          // 주문할 수량
    private String imageUrl;        // 이미지 경로
    private Integer totalPrice;

    public OrderItemDto(OrderItem orderItem) {
        this.itemOptionId = orderItem.getItemOption().getId();
        this.itemName = orderItem.getItemOption().getItem().getItemName();
        this.orderPrice = orderItem.getOrderPrice();
        this.count = orderItem.getCount();
        this.imageUrl = orderItem.getItemOption().getItem().getImageUrl();
        this.totalPrice = this.orderPrice * this.count;
    }

    public boolean isValid() {
        return itemOptionId != null && orderPrice != null && orderPrice > 0 && count != null && count > 0;
    }

    public OrderItem toOrderItem(ItemOption itemOption, Order order) {
        return OrderItem.builder()
                .itemOption(itemOption)
                .orderPrice(this.getOrderPrice())
                .count(this.getCount())
                .imageUrl(itemOption.getItem().getImageUrl())
                .order(order)
                .build();
    }

    public static OrderItemDto from(ItemOption option, int count) {
        Item item = option.getItem();
        int price = item.getPrice();

        return OrderItemDto.builder()
                .itemOptionId(option.getId())
                .itemName(item.getItemName())
                .orderPrice(price)
                .count(count)
                .imageUrl(item.getImageUrl())
                .totalPrice(price * count)
                .build();
    }
}
