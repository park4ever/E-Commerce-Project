package platform.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import platform.ecommerce.exception.order.OrderItemShippedException;

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
    @JoinColumn(name = "item_option_id")
    private ItemOption itemOption;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice;

    private int count;

    @Column(name = "image_url")
    private String imageUrl;

    @Builder
    public OrderItem(ItemOption itemOption, int orderPrice, int count, Order order, String imageUrl) {
        this.itemOption = itemOption;
        this.orderPrice = orderPrice;
        this.count = count;
        if (order != null) {
            this.associateOrder(order);
        }
        this.imageUrl = imageUrl;
    }

    public void associateOrder(Order order) {
        this.order = order;
        if (!order.getOrderItems().contains(this)) {
            order.getOrderItems().add(this);
        }
    }

    public void cancel() {
        if (order.getOrderStatus() == SHIPPED || order.getOrderStatus() == DELIVERED) {
            throw new OrderItemShippedException();
        }

        itemOption.addStock(count);
    }

    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }

    public void updateQuantity(int newQuantity) {
        int quantityDifference = newQuantity - this.count;
        this.count = newQuantity;

        if (quantityDifference > 0) {
            itemOption.removeStock(quantityDifference);
        } else {
            itemOption.addStock(-quantityDifference);
        }
    }

    public void updateOrderPrice(int newPrice) {
        this.orderPrice = newPrice;
    }
}
