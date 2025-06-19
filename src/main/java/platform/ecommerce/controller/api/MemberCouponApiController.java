package platform.ecommerce.controller.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import platform.ecommerce.dto.coupon.MemberCouponResponseDto;
import platform.ecommerce.dto.member.MemberResponseDto;
import platform.ecommerce.exception.unsupported.FeatureNotAvailableException;
import platform.ecommerce.service.MemberCouponService;
import platform.ecommerce.service.MemberService;

import java.util.List;

@RestController
@RequestMapping("/api/member/coupons")
@RequiredArgsConstructor
public class MemberCouponApiController {

    private final MemberCouponService memberCouponService;
    private final MemberService memberService;

    @GetMapping("/usable")
    public ResponseEntity<List<MemberCouponResponseDto>> getUsableCoupons(
            @RequestParam("orderTotal") int orderTotal, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberResponseDto member = memberService.findMember(userDetails.getUsername());

        List<MemberCouponResponseDto> coupons = memberCouponService.getUsableCoupons(member.getMemberId(), orderTotal);

        return ResponseEntity.ok(coupons);
    }

    @GetMapping
    public ResponseEntity<List<MemberCouponResponseDto>> getAllCoupons(Authentication authentication) {
        throw new FeatureNotAvailableException();
    }

    @PostMapping("/{couponId}/issue")
    public ResponseEntity<Long> issueCoupon(@PathVariable("couponId") Long couponId, Authentication authentication) {
        throw new FeatureNotAvailableException();
    }

    @PatchMapping("/{memberCouponId}/use")
    public ResponseEntity<Void> useCoupon(@PathVariable("memberCouponId") Long memberCouponId, Authentication authentication) {
        throw new FeatureNotAvailableException();
    }
}
