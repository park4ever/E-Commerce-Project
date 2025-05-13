package platform.ecommerce.dto.coupon;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import platform.ecommerce.entity.DiscountType;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponCreateRequestDto {

    @NotBlank(message = "쿠폰 이름은 필수 항목입니다.")
    private String name;

    @NotNull(message = "할인 타입은 필수 항목입니다.")
    private DiscountType discountType;

    @Min(value = 1, message = "할인 값은 1 이상이어야 합니다.")
    private int discountValue;

    @Min(value = 0, message = "최소 주문 금액은 0 이상이어야 합니다.")
    private int minOrderAmount;

    @Min(value = 1, message = "최대 할인 금액은 1 이상이어야 합니다.")
    private Integer maxDiscountAmount;  //TODO 정률 할인인 경우 필수 입력하도록 프론트에서 분기 처리

    private LocalDateTime validFrom;

    private LocalDateTime validTo;
}
