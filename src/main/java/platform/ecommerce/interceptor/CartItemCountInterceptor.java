package platform.ecommerce.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import platform.ecommerce.service.CartService;
import platform.ecommerce.service.MemberService;

@Component
@RequiredArgsConstructor
public class CartItemCountInterceptor implements HandlerInterceptor {

    private final CartService cartService;
    private final MemberService memberService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int cartItemCount = 0;

        if (authentication != null && (authentication.getPrincipal() instanceof UserDetails)) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Long memberId = memberService.findMember(userDetails.getUsername()).getMemberId();
            cartItemCount = cartService.getCartItemCount(memberId);
        }

        request.setAttribute("cartItemCount", cartItemCount);
        return true; //요청을 계속 처리하도록 허용
    }
}
