package platform.ecommerce.dto.coupon;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberCouponIssueRequestDto {

    @NotNull(message = "회원 ID는 필수 입력 항목입니다.")
    private Long memberId;

    @NotNull(message = "쿠폰 ID는 필수 입력 항목입니다.")
    private Long couponId;
}
