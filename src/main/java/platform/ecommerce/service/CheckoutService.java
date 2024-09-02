package platform.ecommerce.service;

import platform.ecommerce.dto.OrderSaveRequestDto;

public interface CheckoutService {

    Long checkoutCart(Long memberId);
}
