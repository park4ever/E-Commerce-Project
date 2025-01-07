package platform.ecommerce.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @GetMapping("/update")
    public String profileForm(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberResponseDto member = memberService.findMember(userDetails.getUsername());

        UpdateMemberRequestDto memberDto = memberService.toUpdateDto(member.getMemberId());

        //포맷팅된 생년월일 문자열을 설정
        String formattedDateOfBirth = "";
        if (memberDto.getDateOfBirth() != null) {
            formattedDateOfBirth = memberDto.getDateOfBirth().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        model.addAttribute("formattedDateOfBirth", formattedDateOfBirth);
        model.addAttribute("updateMemberRequestDto", memberDto);
        return "/pages/member/profileForm";
    }

    @PostMapping("/update")
    public String submitProfileUpdate(
            @Valid @ModelAttribute("updateMemberRequestDto") UpdateMemberRequestDto memberDto,
            BindingResult bindingResult, HttpSession session, Model model) {
        if (bindingResult.hasErrors()) {
            return "/pages/member/profileForm";
        }

        session.setAttribute("updateMemberRequestDto", memberDto);

        return "redirect:/member/confirm-password";
    }

    @GetMapping("/confirm-password")
    public String confirmPasswordForm(Model model) {
        model.addAttribute("confirmPasswordDto", new ConfirmPasswordDto());
        return "pages/member/confirmPasswordForm";
    }

    @PostMapping("/confirm-password")
    public String confirmPasswordAndUpdate(
            @Valid @ModelAttribute("confirmPasswordDto") ConfirmPasswordDto passwordDto,
            BindingResult bindingResult, Authentication authentication,
            HttpSession session, SessionStatus sessionStatus, Model model) {
        if (bindingResult.hasErrors()) {
            return "/pages/member/confirmPasswordForm";
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberResponseDto member = memberService.findMember(userDetails.getUsername());

        //세션에서 dto 가져오기
        UpdateMemberRequestDto memberDto = (UpdateMemberRequestDto) session.getAttribute("updateMemberRequestDto");

        //null 체크
        if (memberDto == null) {
            model.addAttribute("error", "세션이 만료되었습니다. 다시 시도해주세요.");
            return "redirect:/member/update";
        }

        //비밀번호 확인 및 회원 정보 업데이트
        memberService.updateMemberWithPasswordCheck(member.getMemberId(), memberDto, passwordDto.getCurrentPassword());

        //세션에서 dto 제거
        session.removeAttribute("updateMemberRequestDto");
        sessionStatus.setComplete();

        return "redirect:/member/profile?success"; //성공 메시지 추가 기능
    }

    @PostMapping("/delete")
    public String deleteMember(@RequestParam("memberId") Long memberId, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberResponseDto member = memberService.findMember(userDetails.getUsername());

        if (!member.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        memberService.deleteMember(memberId);
        SecurityContextHolder.clearContext();

        return "redirect:/logout";
    }
}
