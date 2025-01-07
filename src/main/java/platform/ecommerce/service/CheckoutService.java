package platform.ecommerce.service;

import platform.ecommerce.dto.cart.CartCheckoutDto;

public interface CheckoutService {

    Long checkoutCart(CartCheckoutDto cartCheckoutDto);
}
