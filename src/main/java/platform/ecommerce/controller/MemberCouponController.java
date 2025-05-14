package platform.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import platform.ecommerce.dto.coupon.MemberCouponIssueRequestDto;
import platform.ecommerce.dto.coupon.MemberCouponResponseDto;
import platform.ecommerce.dto.member.MemberResponseDto;
import platform.ecommerce.service.MemberCouponService;
import platform.ecommerce.service.MemberService;

import java.util.List;

@Controller
@RequestMapping("/member/coupons")
@RequiredArgsConstructor
public class MemberCouponController {

    private final MemberCouponService memberCouponService;
    private final MemberService memberService;

    @GetMapping
    public String viewCoupons(Authentication authentication, Model model) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberResponseDto member = memberService.findMember(userDetails.getUsername());

        List<MemberCouponResponseDto> coupons = memberCouponService.getAllCoupons(member.getMemberId());
        model.addAttribute("coupons", coupons);

        return "member/coupon-list";
    }

    @PostMapping("/{couponId}/issue")
    public String issueCoupon(@PathVariable("couponId") Long couponId,
                              Authentication authentication, Model model) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberResponseDto member = memberService.findMember(userDetails.getUsername());

        try {
            MemberCouponIssueRequestDto dto = MemberCouponIssueRequestDto.builder()
                    .memberId(member.getMemberId())
                    .couponId(couponId)
                    .build();
            memberCouponService.issueCoupon(dto);

            return "redirect:/member/coupon?success=true";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/member/coupon?error=" + e.getMessage();
        }
    }
}
