package platform.ecommerce.service;

import platform.ecommerce.dto.cart.CartCheckoutDto;
import platform.ecommerce.dto.cart.CartItemDto;

import java.util.List;

public interface CartService {

    void addItemToCart(Long memberId, Long itemId, int quantity);

    List<CartItemDto> getCartItems(Long memberId);

    void updateItemQuantity(Long memberId, Long itemId, int quantity);

    void removeItemFromCart(Long memberId, Long itemId);

    int calculateCartTotal(List<CartItemDto> cartItems);

    List<CartItemDto> findCartItems(Long memberId);

    CartCheckoutDto prepareCheckout(Long memberId, CartCheckoutDto cartCheckoutDto);

    void clearCartAfterOrder(Long memberId);

    void clearCart(Long memberId);

    int getCartItemCount(Long memberId);
}
