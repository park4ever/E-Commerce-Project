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
import static platform.ecommerce.entity.PaymentMethod.*;

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

    private boolean isPaid = false;

    /* == 연관관계 편의 메서드 == */
    @Builder
    public Order(Member member, LocalDateTime orderDate, OrderStatus orderStatus, List<OrderItem> orderItems, Address shippingAddress, PaymentMethod paymentMethod, String modificationReason) {
        this.member = member;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
        this.modificationReason = modificationReason;
        this.isPaid = paymentMethod != BANK_TRANSFER; //무통장입금일 경우 default -> false

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

    //결제 완료 처리
    public void markAsPaid() {
        this.isPaid = true;
    }

    //주문 취소 가능 여부 확인
    public boolean isCancelable() {
        return orderStatus == PENDING || orderStatus == PROCESSED;
    }

    //배송 전 상태에서 취소
    public void cancel() {
        if (!isCancelable()) {
            throw new IllegalStateException("배송 준비 중인 경우에만 주문 취소가 가능합니다.");
        }
        this.orderStatus = CANCELLED;
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    //배송 전 상태에서 주소 변경
    public void updateShippingAddress(Address newAddress) {
        if (isCancelable()) {
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

    public int getTotalPrice() {
        return orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();
    }
}
