package platform.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.*;
import static platform.ecommerce.entity.OrderStatus.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orderItem")
public class OrderItem extends  BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "orderItem_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice;

    private int count;

    @Column(name = "image_url")
    private String imageUrl; //이미지 경로

    /* == 연관관계 편의 메서드 == */
    public void associateOrder(Order order) {
        this.order = order;
        if (!order.getOrderItems().contains(this)) {
            order.getOrderItems().add(this);
        }
    }

    @Builder
    public OrderItem(Item item, int orderPrice, int count, Order order, String imageUrl) {
        this.item = item;
        this.orderPrice = orderPrice;
        this.count = count;
        if (order != null) {
            this.associateOrder(order);
        }
        this.imageUrl = imageUrl;
    }

    public void cancel() {
        if (order.getOrderStatus() == SHIPPED || order.getOrderStatus() == DELIVERED) {
            throw new IllegalStateException("이미 배송된 상품은 취소할 수 없습니다.");
        }

        getItem().addStock(count);
    }

    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
