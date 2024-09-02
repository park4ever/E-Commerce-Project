package platform.ecommerce.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import platform.ecommerce.dto.MemberSaveRequestDto;
import platform.ecommerce.service.GlobalService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GlobalController {

    private final GlobalService globalService;

    @GetMapping("/")
    public String home() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            log.info("Authenticated user = {}", authentication.getName());
        } else {
            log.info("No authentication information available");
        }

        return "home";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("memberSaveRequestDto", new MemberSaveRequestDto());
        return "pages/signupForm";
    }

    @PostMapping("/signup")
    public String signup(@Valid MemberSaveRequestDto memberSaveRequestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "pages/signupForm";
        }

        globalService.join(memberSaveRequestDto);

        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "pages/loginForm";
    }
}
