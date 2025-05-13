package platform.ecommerce.controller.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import platform.ecommerce.dto.coupon.CouponCreateRequestDto;
import platform.ecommerce.dto.coupon.CouponPageRequestDto;
import platform.ecommerce.dto.coupon.CouponResponseDto;
import platform.ecommerce.dto.coupon.CouponUpdateRequestDto;
import platform.ecommerce.service.CouponService;

@RestController
@RequestMapping("/api/admin/coupons")
@RequiredArgsConstructor
public class CouponApiController {

    private final CouponService couponService;

    @PostMapping
    public ResponseEntity<Long> createCoupon(@RequestBody @Valid CouponCreateRequestDto dto) {
        Long couponId = couponService.createCoupon(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(couponId);
    }

    @PutMapping("/{couponId}")
    public ResponseEntity<Void> updateCoupon(@PathVariable("couponId") Long couponId,
                                             @RequestBody @Valid CouponUpdateRequestDto dto) {
        couponService.updateCoupon(couponId, dto);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{couponId}/disable")
    public ResponseEntity<Void> disableCoupon(@PathVariable("couponId") Long couponId) {
        couponService.disableCoupon(couponId);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{couponId}/enable")
    public ResponseEntity<Void> enableCoupon(@PathVariable("couponId") Long couponId) {
        couponService.enableCoupon(couponId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{couponId}")
    public ResponseEntity<CouponResponseDto> getCoupon(@PathVariable("couponId") Long couponId) {
        CouponResponseDto dto = couponService.getCoupon(couponId);

        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<Page<CouponResponseDto>> getCoupons(@ModelAttribute("requestDto") CouponPageRequestDto requestDto) {
        Page<CouponResponseDto> result = couponService.getCoupons(requestDto);

        return ResponseEntity.ok(result);
    }
}
