package platform.ecommerce.dto.coupon;

import lombok.*;
import platform.ecommerce.entity.Coupon;
import platform.ecommerce.entity.DiscountType;
import platform.ecommerce.entity.MemberCoupon;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberCouponResponseDto {

    private Long memberCouponId;
    private Long couponId;

    private String name;
    private DiscountType discountType;
    private int discountValue;
    private int minOrderAmount;
    private Integer maxDiscountAmount;

    private LocalDateTime validFrom;
    private LocalDateTime validTo;

    private boolean used;
    private LocalDateTime usedAt;

    private boolean usable;

    public static MemberCouponResponseDto from(MemberCoupon mc) {
        Coupon c = mc.getCoupon();

        return MemberCouponResponseDto.builder()
                .memberCouponId(mc.getId())
                .couponId(c.getId())
                .name(c.getName())
                .discountType(c.getDiscountType())
                .discountValue(c.getDiscountValue())
                .minOrderAmount(c.getMinOrderAmount())
                .maxDiscountAmount(c.getMaxDiscountAmount())
                .validFrom(c.getValidFrom())
                .validTo(c.getValidTo())
                .used(mc.isUsed())
                .usedAt(mc.getUsedAt())
                .usable(false)  //사용하지 않음. null 대신 처리하기 쉬운 false로 처리.
                .build();
    }

    public static MemberCouponResponseDto from(MemberCoupon mc, int orderTotal) {
        Coupon c = mc.getCoupon();

        return MemberCouponResponseDto.builder()
                .memberCouponId(mc.getId())
                .couponId(c.getId())
                .name(c.getName())
                .discountType(c.getDiscountType())
                .discountValue(c.getDiscountValue())
                .minOrderAmount(c.getMinOrderAmount())
                .maxDiscountAmount(c.getMaxDiscountAmount())
                .validFrom(c.getValidFrom())
                .validTo(c.getValidTo())
                .used(mc.isUsed())
                .usedAt(mc.getUsedAt())
                .usable(mc.isUsable(orderTotal))
                .build();
    }
}
