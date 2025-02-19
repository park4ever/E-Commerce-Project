package platform.ecommerce.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import platform.ecommerce.dto.item.ItemResponseDto;
import platform.ecommerce.dto.member.MemberSaveRequestDto;
import platform.ecommerce.service.GlobalService;
import platform.ecommerce.service.ItemService;
import platform.ecommerce.service.ReviewService;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GlobalController {

    private final GlobalService globalService;

    @GetMapping("/")
    public String home(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        HttpSession session = request.getSession(false);

        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            log.info("🏠 홈 - 인증된 사용자 : {}", authentication.getName());
        } else {
            log.info("🏠 홈 - 인증되지 않은 사용자");
        }

        if (session != null) {
            log.info("🏠 홈 - 세션 유지 중, ID: {}", session.getId());
        }

        return "home";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("memberSaveRequestDto", new MemberSaveRequestDto());
        return "pages/signupForm";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("memberSaveRequestDto") MemberSaveRequestDto dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "pages/signupForm";
        }

        if (!dto.getPassword().equals(dto.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "error.passwordConfirm", "비밀번호가 일치하지 않습니다.");
            return "pages/signupForm";
        }

        globalService.join(dto);

        return "redirect:/";
    }

    @GetMapping("/login")
    public String loginPage(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        HttpSession session = request.getSession(false);

        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            log.info("🔐 로그인 페이지 - 이미 로그인된 사용자 : {}", authentication.getName());
            log.info("🔐 로그인 페이지 - 세션 유지됨, ID: {}", session != null ? session.getId() : "세션 없음");
            return "redirect:/";
        }

        log.info("🔐 로그인 페이지 - 인증되지 않은 사용자");
        return "pages/loginForm";
    }
}

