package platform.ecommerce.service;

import platform.ecommerce.dto.cart.CartItemDto;
import platform.ecommerce.dto.order.OrderSaveRequestDto;

import java.util.List;

public interface CartService {

    void addToCart(Long memberId, Long itemOptionId, int quantity);

    List<CartItemDto> getCartItems(Long memberId);

    void updateQuantity(Long memberId, Long cartItemId, int quantity);

    void removeFromCart(Long memberId, Long cartItemId);

    int calculateTotalPrice(List<CartItemDto> cartItems);

    List<CartItemDto> findCartItems(Long memberId);

    OrderSaveRequestDto prepareOrderFromCart(Long memberId);

    void clearCart(Long memberId);

    int getCartItemCount(Long memberId);

    void removeOrderedItemsFromCart(Long memberId, List<Long> orderedItemOptionIds);
}
