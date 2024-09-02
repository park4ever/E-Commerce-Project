package platform.ecommerce.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import platform.ecommerce.dto.ChangePasswordRequestDto;
import platform.ecommerce.dto.MemberResponseDto;
import platform.ecommerce.dto.UpdateMemberRequestDto;
import platform.ecommerce.service.MemberService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/profile")
    public String viewProfile(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberResponseDto member = memberService.findMember(userDetails.getUsername());

        model.addAttribute("memberId", member.getMemberId());
        return "/pages/member/profile";
    }

    @GetMapping("/update")
    public String updateMemberForm(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberResponseDto member = memberService.findMember(userDetails.getUsername());

        model.addAttribute("memberId", member.getMemberId());
        model.addAttribute("updateMemberRequestDto", new UpdateMemberRequestDto());
        return "/pages/member/updateProfile";
    }

    @PostMapping("/update")
    public String updateMember(
            @Valid @ModelAttribute("updateMemberRequestDto") UpdateMemberRequestDto updateMemberRequestDto,
            BindingResult bindingResult, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return "/pages/member/updateProfile";
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberResponseDto member = memberService.findMember(userDetails.getUsername());

        memberService.updateMember(member.getMemberId(), updateMemberRequestDto);

        return "redirect:/member/profile";
    }

    @GetMapping("/changePassword")
    public String changePasswordForm(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberResponseDto member = memberService.findMember(userDetails.getUsername());

        model.addAttribute("memberId", member.getMemberId());
        model.addAttribute("changePasswordRequestDto", new ChangePasswordRequestDto());
        return "/pages/member/changePassword";
    }

    @PostMapping("/changePassword")
    public String changePassword(
            @Valid @ModelAttribute("changePasswordRequestDto") ChangePasswordRequestDto changePasswordRequestDto,
            BindingResult bindingResult, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return "/pages/member/changePassword";
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberResponseDto member = memberService.findMember(userDetails.getUsername());

        memberService.changePassword(member.getMemberId(), changePasswordRequestDto);

        return "redirect:/member/profile";
    }

    @PostMapping("/delete")
    public String deleteMember(@RequestParam("memberId") Long memberId, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberResponseDto member = memberService.findMember(userDetails.getUsername());

        if (!member.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        memberService.deleteMember(memberId);

        return "redirect:/logout";
    }
}
