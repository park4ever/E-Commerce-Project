package platform.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import platform.ecommerce.dto.coupon.CouponPageRequestDto;
import platform.ecommerce.entity.Coupon;

public interface CouponRepositoryCustom {

    Page<Coupon> searchCoupons(CouponPageRequestDto requestDto, Pageable pageable);
}
