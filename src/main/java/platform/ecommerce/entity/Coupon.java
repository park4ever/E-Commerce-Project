package platform.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.*;
import static lombok.AccessLevel.*;
import static platform.ecommerce.entity.DiscountType.*;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "coupon")
public class Coupon extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "coupon_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(STRING)
    @Column(nullable = false)
    private DiscountType discountType;

    @Column(nullable = false)
    private int discountValue;

    @Column(nullable = false)
    private int minOrderAmount;

    private Integer maxDiscountAmount;

    private LocalDateTime validFrom;

    private LocalDateTime validTo;

    @Column(nullable = false)
    private boolean isEnabled = true;

    public boolean isValidNow() {
        LocalDateTime now = LocalDateTime.now();
        return isEnabled && (validFrom == null || !now.isBefore(validFrom)) && (validTo == null || !now.isAfter(validTo));
    }

    public int calculateDiscountAmount(int orderTotal) {
        if (discountType == AMOUNT) {
            return discountValue;
        } else {
            int percentDiscount = (int) Math.floor(orderTotal * (discountValue / 100.0));
            if (maxDiscountAmount != null) {
                return Math.min(percentDiscount, maxDiscountAmount);
            }
            return percentDiscount;
        }
    }

    public void updateCouponInfo(String name, DiscountType discountType,
                                 int discountValue, int minOrderAmount, Integer maxDiscountAmount,
                                 LocalDateTime validFrom, LocalDateTime validTo) {
        this.name = name;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minOrderAmount = minOrderAmount;
        this.maxDiscountAmount = maxDiscountAmount;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    public void disable() {
        this.isEnabled = false;
    }

    public void enable() {
        this.isEnabled = true;
    }
}
