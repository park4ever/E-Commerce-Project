package platform.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "member_coupon")
public class MemberCoupon extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "member_coupon_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    private boolean used = false;

    private LocalDateTime usedAt;

    private LocalDateTime issuedAt;

    public boolean isUsable(int orderTotal) {
        return !used
                && (issuedAt == null || !issuedAt.isAfter(LocalDateTime.now()))
                && coupon.isValidNow()
                && orderTotal >= coupon.getMinOrderAmount();
    }

    public int getDiscountAmount(int orderTotal) {
        return coupon.calculateDiscountAmount(orderTotal);
    }

    public void markAsUsed() {
        this.used = true;
        this.usedAt = LocalDateTime.now();
    }
}
