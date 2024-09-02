package platform.ecommerce.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import platform.ecommerce.dto.CartCheckoutDto;
import platform.ecommerce.dto.OrderItemDto;
import platform.ecommerce.dto.OrderSaveRequestDto;
import platform.ecommerce.service.CartService;
import platform.ecommerce.service.CheckoutService;
import platform.ecommerce.service.OrderService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    private final CartService cartService;
    private final OrderService orderService;

    @Override
    @Transactional
    public Long checkoutCart(Long memberId) {
        CartCheckoutDto cartCheckoutDto = cartService.prepareCheckout(memberId);

        OrderSaveRequestDto orderSaveRequestDto = convertToOrderSaveRequestDto(cartCheckoutDto);
        //장바구니 비우기
        cartService.clearCartAfterOrder(memberId);

        return orderService.createOrder(orderSaveRequestDto);
    }

    private OrderSaveRequestDto convertToOrderSaveRequestDto(CartCheckoutDto cartCheckoutDto) {
        List<OrderItemDto> orderItems = cartCheckoutDto.getCartItems().stream()
                .map(cartItemDto -> new OrderItemDto(
                        cartItemDto.getItemId(),
                        cartItemDto.getItemName(),
                        cartItemDto.getItemPrice(),  // itemPrice -> orderPrice 매핑
                        cartItemDto.getQuantity()    // quantity -> count 매핑
                ))
                .collect(Collectors.toList());

        return new OrderSaveRequestDto(cartCheckoutDto.getMemberId(), LocalDateTime.now(), orderItems);
    }
}
