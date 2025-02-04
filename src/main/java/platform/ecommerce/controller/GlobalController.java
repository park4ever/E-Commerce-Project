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

        if (authentication != null) {
            log.info("🏠 Home - Authenticated user : {}", authentication.getName());
            log.info("🏠 Home - Authentication Class : {}", authentication.getClass().getName());
            log.info("🏠 Home - Authorities : {}", authentication.getAuthorities());
            log.info("🏠 Home - Is Anonymous : {}", authentication instanceof AnonymousAuthenticationToken);
        } else {
            log.info("🏠 Home - No Authentication information");
        }

        if (session != null) {
            log.info("🏠 Home - Session ID : {}", session.getId());
        } else {
            log.info("🏠 Home - No Session available");
        }

        /*//상품 목록 가져오기
        List<ItemResponseDto> items = itemService.findItems();

        //각 상품의 평균 평점 가져오기
        for (ItemResponseDto item : items) {
            double averageRating = reviewService.calculateAverageRating(item.getId());
            item.setAverageRating(averageRating);
        }

        model.addAttribute("items", items);*/
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

//    @GetMapping("/login")
//    public String login(HttpServletRequest request, Model model) {
//        //이메일 입력 값 가져오기
//        String email = request.getParameter("email");
//
//        //이메일 존재 여부 확인
//        boolean emailError = email != null && !globalService.emailExists(email); //이메일이 존재하지 않으면 오류 발생
//
//        //비밀번호 에러 상태 가져오기
//        Boolean passwordError = (Boolean) request.getSession().getAttribute("passwordError");
//
//        //모델에 에러 상태 추가
//        model.addAttribute("emailError", emailError);
//        model.addAttribute("passwordError", passwordError != null && passwordError);
//
//        //세션에서 에러 정보 제거
//        request.getSession().removeAttribute("emailError");
//        request.getSession().removeAttribute("passwordError");
//
//        return "pages/loginForm";
//    } //TODO

    @GetMapping("/login")
    public String loginPage(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        HttpSession session = request.getSession(true);

        log.info("!!! Authentication : {}", authentication);
        if (authentication != null) {
            log.info("Login - Authenticated : {}", authentication.isAuthenticated());
            log.info("Login - Principal : {}", authentication.getPrincipal());
            log.info("Login - Authentication Class : {}", authentication.getClass().getName());
            log.info("Login - Is Anonymous : {}", authentication instanceof AnonymousAuthenticationToken);
            log.info("Login - Session ID : {}", session.getId());
        }
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)
                && request.getSession(false) != null) {
            log.info("Login - User is already authenticated, redirecting to home.");
            log.info("Login - Session ID : {}", session.getId());
            return "redirect:/home";
        }
        return "pages/loginForm";
    }
}
