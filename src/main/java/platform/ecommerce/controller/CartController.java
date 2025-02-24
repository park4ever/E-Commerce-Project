package platform.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import platform.ecommerce.dto.cart.CartItemDto;
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
        int cartItemsTotal = cartService.calculateCartTotal(cartItems);
        model.addAttribute("cartItemsTotal", cartItemsTotal);

        log.debug("장바구니 상품 개수 = {}", cartItems.size());
        return "/pages/cart/cartList";
    }

    @PostMapping("/add")
    public String addItemToCart(@ModelAttribute("cartItemDto") CartItemDto cartItemDto,
                                Authentication authentication) {
        log.info("itemId = {}, quantity = {}", cartItemDto.getItemId(), cartItemDto.getQuantity());

        if (cartItemDto.getItemId() == null || cartItemDto.getQuantity() <= 0) {
            throw new IllegalArgumentException("유효하지 않은 상품 ID 또는 수량입니다.");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Long memberId = memberService.findMember(userDetails.getUsername()).getMemberId();
        cartService.addItemToCart(memberId, cartItemDto.getItemId(), cartItemDto.getQuantity());
        return "redirect:/item/" + cartItemDto.getItemId();
    }

    @PostMapping("/update")
    public String updateCartItem(@ModelAttribute("cartItemDto") CartItemDto cartItemDto,
                                 Authentication authentication) {
        log.info("itemId = {}, quantity = {}", cartItemDto.getItemId(), cartItemDto.getQuantity());

        if (cartItemDto.getItemId() == null || cartItemDto.getQuantity() <= 0) {
            throw new IllegalArgumentException("유효하지 않은 상품 ID 또는 수량입니다.");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Long memberId = memberService.findMember(userDetails.getUsername()).getMemberId();
        cartService.updateItemQuantity(memberId, cartItemDto.getItemId(), cartItemDto.getQuantity());
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeItemFromCart(@RequestParam("itemId") Long itemId,
                                     Authentication authentication) {
        log.info("itemId = {}", itemId);

        if (itemId == null) {
            throw new IllegalArgumentException("유효하지 않은 상품 ID입니다.");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Long memberId = memberService.findMember(userDetails.getUsername()).getMemberId();
        cartService.removeItemFromCart(memberId, itemId);
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