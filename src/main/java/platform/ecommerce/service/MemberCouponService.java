package platform.ecommerce.service;

import platform.ecommerce.dto.coupon.MemberCouponIssueRequestDto;
import platform.ecommerce.dto.coupon.MemberCouponResponseDto;
import platform.ecommerce.entity.MemberCoupon;

import java.util.List;

public interface MemberCouponService {

    Long issueCoupon(MemberCouponIssueRequestDto dto);

    List<MemberCouponResponseDto> getUsableCoupons(Long memberId, int orderTotal);

    List<MemberCouponResponseDto> getAllCoupons(Long memberId);

    List<MemberCouponResponseDto> getAllCouponsWithUsability(Long memberId, int orderTotal);

    MemberCoupon getOwnedCouponOrThrow(Long memberCouponId, Long memberId);

    void useCoupon(Long memberCouponId, Long memberId);
}
