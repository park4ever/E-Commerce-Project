package platform.ecommerce.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import platform.ecommerce.dto.member.MemberSaveRequestDto;
import platform.ecommerce.service.GlobalService;

@Controller
@RequiredArgsConstructor
public class GlobalController {

    private final GlobalService globalService;

    @GetMapping("/")
    public String home() {
        return "pages/home/home";
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
    public String loginPage() {
        return "pages/loginForm";
    }
}