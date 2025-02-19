package platform.ecommerce.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

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
                                 BindingResult bindingResult, Authentication authentication, HttpSession session, Model model) {
        if (bindingResult.hasErrors() || passwordDto.getCurrentPassword() == null) {
            model.addAttribute("passwordError", "비밀번호를 입력하세요.");
            return "/pages/member/checkPassword";
        }

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberResponseDto member = memberService.findMember(userDetails.getUsername());

        boolean isPasswordValid = memberService.checkPassword(member.getMemberId(), passwordDto.getCurrentPassword());
        if (!isPasswordValid) {
            model.addAttribute("passwordError", "비밀번호가 일치하지 않습니다.");
            return "/pages/member/checkPassword";
        }

        UsernamePasswordAuthenticationToken newAuth =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        session.setAttribute("passwordVerified", true);

        log.info("✅ 비밀번호 확인 완료. SecurityContext 및 세션 업데이트 완료.");
        return "redirect:/member/update-form";
    }

    @GetMapping("/update-form")
    public String profileForm(HttpSession session, Model model, Authentication authentication) {
        Boolean isVerified = (Boolean) session.getAttribute("passwordVerified");
        log.info("=== [profileForm] passwordVerified: {}", isVerified);

        if (isVerified == null || !isVerified) {
            log.warn("=== [profileForm] 세션이 만료되었거나 인증되지 않음. 다시 인증 필요.");
            session.removeAttribute("passwordVerified");
            model.addAttribute("passwordError", "세션이 만료되었습니다. 다시 인증해주세요.");
            return "redirect:/member/check-password";
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberResponseDto member = memberService.findMember(userDetails.getUsername());

        UpdateMemberRequestDto memberDto = memberService.toUpdateDto(member.getMemberId());
        model.addAttribute("updateMemberRequestDto", memberDto);

        return "/pages/member/profileForm";
    }

    @PostMapping("/update")
    public String submitProfileUpdate(
            @Valid @ModelAttribute("updateMemberRequestDto") UpdateMemberRequestDto memberDto,
            BindingResult bindingResult, HttpSession session, Model model, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return "/pages/member/profileForm";
        }

        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            MemberResponseDto member = memberService.findMember(userDetails.getUsername());

            memberService.updateMember(member.getMemberId(), memberDto);
            session.removeAttribute("passwordVerified");
        } catch (IllegalArgumentException e) {
            model.addAttribute("passwordError", e.getMessage());
            return "/pages/member/profileForm";
        }

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
