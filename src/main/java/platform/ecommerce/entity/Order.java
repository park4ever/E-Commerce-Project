package platform.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.*;
import static platform.ecommerce.entity.OrderStatus.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "order", cascade = ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    /* == 연관관계 편의 메서드 == */
    @Builder
    public Order(Member member, LocalDateTime orderDate, OrderStatus orderStatus, List<OrderItem> orderItems) {
        this.member = member;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        if (orderItems != null) {
            for (OrderItem orderItem : orderItems) {
                this.addOrderItem(orderItem);
            }
        }
    }

    public void updateStatus(OrderStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("주문 상태가 null 값일 수 없습니다.");
        }
        this.orderStatus = status;
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.associateOrder(this);
    }

    /* == 비즈니스 로직 == */
    public void cancel() {
        if (orderStatus == DELIVERED || orderStatus == SHIPPED) {
            throw new IllegalStateException("이미 배송이 완료된 상품은 취소가 불가능합니다.");
        }

        orderStatus = CANCELLED;
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    /* == 조회 로직 == */

    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }
}
