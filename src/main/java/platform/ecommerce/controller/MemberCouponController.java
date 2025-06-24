package platform.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import platform.ecommerce.config.auth.LoginMember;
import platform.ecommerce.dto.coupon.MemberCouponIssueRequestDto;
import platform.ecommerce.dto.coupon.MemberCouponResponseDto;
import platform.ecommerce.dto.member.LoginMemberDto;
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
    public String viewCoupons(@LoginMember LoginMemberDto member, Model model) {
        List<MemberCouponResponseDto> coupons = memberCouponService.getAllCoupons(member.id());
        model.addAttribute("coupons", coupons);

        return "member/coupon-list";
    }

    @PostMapping("/{couponId}/issue")
    public String issueCoupon(@PathVariable("couponId") Long couponId,
                              @LoginMember LoginMemberDto member,
                              RedirectAttributes redirectAttributes) {
        try {
            MemberCouponIssueRequestDto dto = MemberCouponIssueRequestDto.builder()
                    .memberId(member.id())
                    .couponId(couponId)
                    .build();
            memberCouponService.issueCoupon(dto);
            redirectAttributes.addFlashAttribute("success", "쿠폰이 발급되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "메시지");
        }

        return "redirect:/member/coupons";
    }
}