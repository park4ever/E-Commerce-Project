package platform.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.*;

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

    /* == 연관관계 편의 메서드 == */
    public void associateOrder(Order order) {
        this.order = order;
        if (!order.getOrderItems().contains(this)) {
            order.getOrderItems().add(this);
        }
    }

    @Builder
    public OrderItem(Item item, int orderPrice, int count, Order order) {
        this.item = item;
        this.orderPrice = orderPrice;
        this.count = count;
        if (order != null) {
            this.associateOrder(order);
        }
    }

    /* == 비즈니스 로직 == */
    public void cancel() {
        getItem().addStock(count);
    }

    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
