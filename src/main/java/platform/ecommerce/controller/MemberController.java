package platform.ecommerce.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import platform.ecommerce.config.auth.LoginMember;
import platform.ecommerce.dto.member.*;
import platform.ecommerce.service.MemberService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/profile")
    public String viewProfile(@LoginMember LoginMemberDto member, Model model) {
        MemberProfileDto profileDto = memberService.toProfileDto(member.id());
        model.addAttribute("member", profileDto);

        return "/pages/member/profile";
    }

    @GetMapping("/update")
    public String redirectToPasswordVerification() {
        return "redirect:/member/check-password";
    }

    @GetMapping("/check-password")
    public String showPasswordCheckForm(Model model) {
        model.addAttribute("confirmPasswordDto", new ConfirmPasswordDto());
        return "/pages/member/checkPassword";
    }

    @PostMapping("/check-password")
    public String verifyPassword(@Valid @ModelAttribute("confirmPasswordDto") ConfirmPasswordDto passwordDto,
                                 @LoginMember LoginMemberDto member, BindingResult bindingResult,
                                 HttpSession session, Model model) {
        if (bindingResult.hasErrors() || passwordDto.getCurrentPassword() == null) {
            model.addAttribute("passwordError", "비밀번호를 입력하세요.");
            return "/pages/member/checkPassword";
        }

        boolean isPasswordValid = memberService.checkPassword(member.id(), passwordDto.getCurrentPassword());
        if (!isPasswordValid) {
            model.addAttribute("passwordError", "비밀번호가 일치하지 않습니다.");
            return "/pages/member/checkPassword";
        }

        session.setAttribute("passwordVerified", true);

        return "redirect:/member/update-form";
    }

    @GetMapping("/update-form")
    public String profileForm(@LoginMember LoginMemberDto member, HttpSession session, Model model) {
        Boolean isVerified = (Boolean) session.getAttribute("passwordVerified");
        if (isVerified == null || !isVerified) {
            session.removeAttribute("passwordVerified");
            model.addAttribute("passwordError", "세션이 만료되었습니다. 다시 인증해주세요.");
            return "redirect:/member/check-password";
        }

        UpdateMemberRequestDto memberDto = memberService.toUpdateDto(member.id());
        model.addAttribute("updateMemberRequestDto", memberDto);

        return "/pages/member/profileForm";
    }

    @PostMapping("/update")
    public String submitProfileUpdate(
            @Valid @ModelAttribute("updateMemberRequestDto") UpdateMemberRequestDto memberDto,
            @LoginMember LoginMemberDto member, BindingResult bindingResult, HttpSession session, Model model) {
        if (bindingResult.hasErrors()) {
            return "/pages/member/profileForm";
        }

        try {
            memberService.updateMember(member.id(), memberDto);
            session.removeAttribute("passwordVerified");
        } catch (IllegalArgumentException e) {
            model.addAttribute("passwordError", e.getMessage());
            return "/pages/member/profileForm";
        }

        return "redirect:/member/profile?success";
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteMember(@RequestParam("memberId") Long memberId, @LoginMember LoginMemberDto member) {
        if (!member.id().equals(memberId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
        }

        memberService.deleteMember(memberId);
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok().body("회원 탈퇴가 완료되었습니다.");
    }
}