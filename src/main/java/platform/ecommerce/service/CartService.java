package platform.ecommerce.service;

import platform.ecommerce.dto.cart.CartItemDto;
import platform.ecommerce.dto.order.OrderSaveRequestDto;

import java.util.List;

public interface CartService {

    void addItemToCart(Long memberId, Long itemId, int quantity);

    List<CartItemDto> getCartItems(Long memberId);

    void updateItemQuantity(Long memberId, Long itemId, int quantity);

    void removeItemFromCart(Long memberId, Long itemId);

    int calculateCartTotal(List<CartItemDto> cartItems);

    List<CartItemDto> findCartItems(Long memberId);

    OrderSaveRequestDto prepareOrderFromCart(Long memberId);

    void clearCartAfterOrder(Long memberId);

    void clearCart(Long memberId);

    int getCartItemCount(Long memberId);
}
