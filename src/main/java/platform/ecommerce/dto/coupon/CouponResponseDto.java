package platform.ecommerce.dto.coupon;

import lombok.*;
import platform.ecommerce.entity.Coupon;
import platform.ecommerce.entity.DiscountType;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponResponseDto {

    private Long id;
    private String name;
    private DiscountType discountType;
    private int discountValue;
    private int minOrderAmount;
    private Integer maxDiscountAmount;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private boolean isEnabled;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    public static CouponResponseDto from(Coupon coupon) {
        return CouponResponseDto.builder()
                .id(coupon.getId())
                .name(coupon.getName())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .minOrderAmount(coupon.getMinOrderAmount())
                .maxDiscountAmount(coupon.getMaxDiscountAmount())
                .validFrom(coupon.getValidFrom())
                .validTo(coupon.getValidTo())
                .isEnabled(coupon.isEnabled())
                .createdDate(coupon.getCreatedDate())
                .lastModifiedDate(coupon.getLastModifiedDate())
                .build();
    }
}
