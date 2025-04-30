package platform.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import platform.ecommerce.dto.cart.CartItemDto;
import platform.ecommerce.dto.cart.CartUpdateRequest;
import platform.ecommerce.dto.member.MemberResponseDto;
import platform.ecommerce.service.CartService;
import platform.ecommerce.service.MemberService;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final MemberService memberService;

    @GetMapping
    public String viewCart(Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication == null || !((authentication.getPrincipal()) instanceof UserDetails)) {
            redirectAttributes.addFlashAttribute("errorMessage", "장바구니는 로그인한 사용자만 확인 가능합니다.");
            return "redirect:/login";
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberResponseDto member = memberService.findMember(userDetails.getUsername());

        List<CartItemDto> cartItems = cartService.getCartItems(member.getMemberId());
        model.addAttribute("cartItems", cartItems);
        int cartItemsTotal = cartService.calculateTotalPrice(cartItems);
        model.addAttribute("cartItemsTotal", cartItemsTotal);

        return "/pages/cart/cart-list";
    }

    @PostMapping("/add")
    public String addItemToCart(@ModelAttribute("cartItemDto") CartItemDto cartItemDto,
                                Authentication authentication) {
        if (cartItemDto.getItemOptionId() == null || cartItemDto.getQuantity() <= 0) {
            throw new IllegalArgumentException("유효하지 않은 상품 ID 또는 수량입니다.");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Long memberId = memberService.findMember(userDetails.getUsername()).getMemberId();
        cartService.addToCart(memberId, cartItemDto.getItemOptionId(), cartItemDto.getQuantity());

        return "redirect:/item/" + cartItemDto.getItemOptionId();
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateCartItem(@RequestBody CartUpdateRequest request,
                                            Authentication authentication) {
        if (request.getCartItemId() == null || request.getQuantity() == null || request.getQuantity() < 1 || request.getQuantity() > 100) {
            return ResponseEntity.badRequest().body("{\"success\": false, \"message\": \"수량은 1~100 사이여야 합니다.\"}");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Long memberId = memberService.findMember(userDetails.getUsername()).getMemberId();

        cartService.updateQuantity(memberId, request.getCartItemId(), request.getQuantity());

        int updatedTotal = cartService.calculateTotalPrice(cartService.getCartItems(memberId)); // 총 금액 업데이트

        return ResponseEntity.ok("{\"success\": true, \"cartTotal\": " + updatedTotal + "}");
    }


    @PostMapping("/remove")
    public String removeItemFromCart(@RequestParam("cartItemId") Long cartItemId,
                                     Authentication authentication) {
        if (cartItemId == null) {
            throw new IllegalArgumentException("유효하지 않은 상품 ID입니다.");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Long memberId = memberService.findMember(userDetails.getUsername()).getMemberId();
        cartService.removeFromCart(memberId, cartItemId);

        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Long memberId = memberService.findMember(userDetails.getUsername()).getMemberId();
        cartService.clearCart(memberId);

        return "redirect:/cart";
    }
}