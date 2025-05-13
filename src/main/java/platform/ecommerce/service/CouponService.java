package platform.ecommerce.service;

import org.springframework.data.domain.Page;
import platform.ecommerce.dto.coupon.CouponCreateRequestDto;
import platform.ecommerce.dto.coupon.CouponPageRequestDto;
import platform.ecommerce.dto.coupon.CouponResponseDto;
import platform.ecommerce.dto.coupon.CouponUpdateRequestDto;

public interface CouponService {

    Long createCoupon(CouponCreateRequestDto dto);

    void updateCoupon(Long couponId, CouponUpdateRequestDto dto);

    void disableCoupon(Long couponId);

    void enableCoupon(Long couponId);

    CouponResponseDto getCoupon(Long couponId);

    Page<CouponResponseDto> getCoupons(CouponPageRequestDto dto);
}
