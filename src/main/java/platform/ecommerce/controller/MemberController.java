package platform.ecommerce.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import platform.ecommerce.dto.member.ConfirmPasswordDto;
import platform.ecommerce.dto.member.MemberProfileDto;
import platform.ecommerce.dto.member.MemberResponseDto;
import platform.ecommerce.dto.member.UpdateMemberRequestDto;
import platform.ecommerce.service.MemberService;

import java.time.format.DateTimeFormatter;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/profile")
    public String viewProfile(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberResponseDto member = memberService.findMember(userDetails.getUsername());

        MemberProfileDto profileDto = memberService.toProfileDto(member.getMemberId());
        model.addAttribute("member", profileDto);

        return "/pages/member/profile";
    }

    /**
     * 프로필 수정 전에 비밀번호 확인 페이지로 이동
     */
    @GetMapping("/update")
    public String redirectToPasswordVerification() {
        return "redirect:/member/check-password";
    }

    /**
     * 비밀번호 확인 페이지
     */
    @GetMapping("/check-password")
    public String showPasswordCheckForm(Model model) {
        model.addAttribute("confirmPasswordDto", new ConfirmPasswordDto());
        return "/pages/member/checkPassword";
    }

    /**
     * 비밀번호 확인 후 수정 페이지로 이동
     */
    @PostMapping("/check-password")
    public String verifyPassword(
            @Valid @ModelAttribute("confirmPasswordDto") ConfirmPasswordDto passwordDto,
            BindingResult bindingResult, Authentication authentication,
            HttpSession session, Model model) {
        if (bindingResult.hasErrors() || passwordDto.getCurrentPassword() == null) {
            model.addAttribute("passwordError", "비밀번호를 입력하세요.");
            return "/pages/member/checkPassword";
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberResponseDto member = memberService.findMember(userDetails.getUsername());

        boolean isPasswordValid = memberService.checkPassword(member.getMemberId(), passwordDto.getCurrentPassword());

        if (!isPasswordValid) {
            model.addAttribute("passwordError", "비밀번호가 일치하지 않습니다.");
            return "/pages/member/checkPassword";
        }

        //인증 성공 -> 세선에 저장
        session.setAttribute("passwordVerified", true);

        return "redirect:/member/update-form";
    }

    /**
     * 프로실 수정 폼(비밀번호 확인 후 접근 가능)
     */
    @GetMapping("/update-form")
    public String profileForm(HttpSession session, Model model, Authentication authentication) {
        //비밀번호 확인이 된 상태가 아니라면 redirect
        Boolean isVerified = (Boolean) session.getAttribute("passwordVerified");

        if (isVerified == null || !isVerified) {
            model.addAttribute("passwordError", "세션이 만료되었습니다. 다시 인증해주세요.");
            return "redirect:/member/check-password";
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberResponseDto member = memberService.findMember(userDetails.getUsername());

        UpdateMemberRequestDto memberDto = memberService.toUpdateDto(member.getMemberId());

        model.addAttribute("updateMemberRequestDto", memberDto);

        return "/pages/member/profileForm";
    }

    /**
     * 최종 프로필 수정 요청 처리
     */
    @PostMapping("/update")
    public String submitProfileUpdate(
            @Valid @ModelAttribute("updateMemberRequestDto") UpdateMemberRequestDto memberDto,
            BindingResult bindingResult, HttpSession session, Model model, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return "/pages/member/profileForm";
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberResponseDto member = memberService.findMember(userDetails.getUsername());

        memberService.updateMember(member.getMemberId(), memberDto);

        //인증 완료 후, 세션 제거
        session.removeAttribute("passwordVerified");

        return "redirect:/member/profile?success";
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteMember(@RequestParam("memberId") Long memberId, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberResponseDto member = memberService.findMember(userDetails.getUsername());

        if (!member.getMemberId().equals(memberId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
        }

        memberService.deleteMember(memberId);
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok().body("회원 탈퇴가 완료되었습니다.");
    }
}
