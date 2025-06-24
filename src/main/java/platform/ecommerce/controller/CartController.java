package platform.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import platform.ecommerce.config.auth.LoginMember;
import platform.ecommerce.dto.cart.CartItemDto;
import platform.ecommerce.dto.cart.CartUpdateRequest;
import platform.ecommerce.dto.member.LoginMemberDto;
import platform.ecommerce.exception.cart.InvalidCartItemException;
import platform.ecommerce.service.CartService;

import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public String viewCart(@LoginMember LoginMemberDto member, Model model) {
        List<CartItemDto> cartItems = cartService.getCartItems(member.id());
        model.addAttribute("cartItems", cartItems);
        int cartItemsTotal = cartService.calculateTotalPrice(cartItems);
        model.addAttribute("cartItemsTotal", cartItemsTotal);

        return "/pages/cart/cart-list";
    }

    @PostMapping("/add")
    public String addItemToCart(@ModelAttribute("cartItemDto") CartItemDto cartItemDto, @LoginMember LoginMemberDto member) {
        if (cartItemDto.getItemOptionId() == null || cartItemDto.getQuantity() <= 0) {
            throw new InvalidCartItemException();
        }

        cartService.addToCart(member.id(), cartItemDto.getItemOptionId(), cartItemDto.getQuantity());

        return "redirect:/item/" + cartItemDto.getItemOptionId();
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateCartItem(@RequestBody CartUpdateRequest request,
                                            @LoginMember LoginMemberDto member) {
        if (request.getCartItemId() == null || request.getQuantity() == null || request.getQuantity() < 1 || request.getQuantity() > 100) {
            return ResponseEntity.badRequest().body("{\"success\": false, \"message\": \"수량은 1~100 사이여야 합니다.\"}");
        }

        cartService.updateQuantity(member.id(), request.getCartItemId(), request.getQuantity());

        int updatedTotal = cartService.calculateTotalPrice(cartService.getCartItems(member.id())); // 총 금액 업데이트

        return ResponseEntity.ok("{\"success\": true, \"cartTotal\": " + updatedTotal + "}");
    }


    @PostMapping("/remove")
    public String removeItemFromCart(@RequestParam("cartItemId") Long cartItemId, @LoginMember LoginMemberDto member) {
        if (cartItemId == null) {
            throw new InvalidCartItemException();
        }

        cartService.removeFromCart(member.id(), cartItemId);

        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(@LoginMember LoginMemberDto member) {
        cartService.clearCart(member.id());

        return "redirect:/cart";
    }
}