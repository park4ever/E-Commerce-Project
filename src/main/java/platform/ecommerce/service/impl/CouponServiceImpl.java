package platform.ecommerce.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import platform.ecommerce.dto.coupon.CouponCreateRequestDto;
import platform.ecommerce.dto.coupon.CouponPageRequestDto;
import platform.ecommerce.dto.coupon.CouponResponseDto;
import platform.ecommerce.dto.coupon.CouponUpdateRequestDto;
import platform.ecommerce.entity.Coupon;
import platform.ecommerce.exception.coupon.CouponNotFoundException;
import platform.ecommerce.repository.CouponRepository;
import platform.ecommerce.service.CouponService;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    @Override
    public Long createCoupon(CouponCreateRequestDto dto) {
        Coupon coupon = Coupon.builder()
                .name(dto.getName())
                .discountType(dto.getDiscountType())
                .discountValue(dto.getDiscountValue())
                .minOrderAmount(dto.getMinOrderAmount())
                .maxDiscountAmount(dto.getMaxDiscountAmount())
                .validFrom(dto.getValidFrom())
                .validTo(dto.getValidTo())
                .isEnabled(true)
                .build();
        couponRepository.save(coupon);

        return coupon.getId();
    }

    @Override
    public void updateCoupon(Long couponId, CouponUpdateRequestDto dto) {
        Coupon coupon = findByIdOrThrow(couponId);

        coupon.updateCouponInfo(
                dto.getName(),
                dto.getDiscountType(),
                dto.getDiscountValue(),
                dto.getMinOrderAmount(),
                dto.getMaxDiscountAmount(),
                dto.getValidFrom(),
                dto.getValidTo()
        );
    }

    @Override
    public void disableCoupon(Long couponId) {
        Coupon coupon = findByIdOrThrow(couponId);
        coupon.disable();
    }

    @Override
    public void enableCoupon(Long couponId) {
        Coupon coupon = findByIdOrThrow(couponId);
        coupon.enable();
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponseDto getCoupon(Long couponId) {
        Coupon coupon = findByIdOrThrow(couponId);

        return CouponResponseDto.from(coupon);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CouponResponseDto> getCoupons(CouponPageRequestDto dto) {
        Page<Coupon> coupons = couponRepository.searchCoupons(dto, dto.toPageable());

        return coupons.map(CouponResponseDto::from);
    }

    private Coupon findByIdOrThrow(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(CouponNotFoundException::new);
    }
}
