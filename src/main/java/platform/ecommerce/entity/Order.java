package platform.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import platform.ecommerce.dto.order.OrderSaveRequestDto;

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

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "order", cascade = ALL, orphanRemoval = true)
    @BatchSize(size = 100)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Embedded
    private Address shippingAddress;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private String modificationReason;

    /* == 연관관계 편의 메서드 == */
    @Builder
    public Order(Member member, LocalDateTime orderDate, OrderStatus orderStatus, List<OrderItem> orderItems, Address shippingAddress, PaymentMethod paymentMethod, String modificationReason) {
        this.member = member;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
        this.modificationReason = modificationReason;
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
    //배송 전 상태에서 취소
    public void cancel() {
        if (orderStatus == PENDING || orderStatus == PROCESSED) {
            orderStatus = CANCELLED;
            for (OrderItem orderItem : orderItems) {
                orderItem.cancel();
            }
        } else {
            throw new IllegalStateException("배송 준비중인 경우에만 주문 취소가 가능합니다.");
        }
    }

    //배송 전 상태에서 주소 변경
    public void updateShippingAddress(Address newAddress) {
        if (orderStatus == PENDING || orderStatus == PROCESSED) {
            this.shippingAddress = newAddress;
        } else {
            throw new IllegalStateException("배송 준비 중인 경우에만 주소 변경이 가능합니다.");
        }
    }

    //교환 및 환불 요청
    public void requestRefund(String reason) {
        if (orderStatus == DELIVERED) {
            this.modificationReason = reason;
            this.orderStatus = REFUND_REQUESTED;
        } else {
            throw new IllegalStateException("배송 완료 후에만 환불 요청이 가능합니다.");
        }
    }

    public void requestExchange(String reason) {
        if (orderStatus == DELIVERED) {
            this.modificationReason = reason;
            this.orderStatus = EXCHANGE_REQUESTED;
        } else {
            throw new IllegalStateException("배송 완료 후에만 교환 요청이 가능합니다.");
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
