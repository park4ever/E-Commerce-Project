package platform.ecommerce.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import platform.ecommerce.dto.cart.CartCheckoutDto;
import platform.ecommerce.dto.order.OrderItemDto;
import platform.ecommerce.dto.order.OrderSaveRequestDto;
import platform.ecommerce.entity.Address;
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
    public Long checkoutCart(CartCheckoutDto cartCheckoutDto) {
        validateCartCheckoutDto(cartCheckoutDto);

        //장바구니 비우기
        cartService.clearCartAfterOrder(cartCheckoutDto.getMemberId());

        OrderSaveRequestDto orderSaveRequestDto = convertToOrderSaveRequestDto(cartCheckoutDto);

        return orderService.createOrder(orderSaveRequestDto);
    }

    private void validateCartCheckoutDto(CartCheckoutDto dto) {
        /*if (dto.getShippingAddress() == null || dto.getShippingAddress().getStreet() == null || dto.getShippingAddress().getStreet().isEmpty()) {
            throw new IllegalArgumentException("배송할 주소는 필수 입력 값입니다.");
        }*/ //TODO

        if (dto.getPaymentMethod() == null) {
            throw new IllegalArgumentException("결제 방식을 선택해주세요.");
        }

        if (dto.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("장바구니에 상품이 없습니다.");
        }
    }

    private OrderSaveRequestDto convertToOrderSaveRequestDto(CartCheckoutDto cartCheckoutDto) {
        List<OrderItemDto> orderItems = cartCheckoutDto.getCartItems().stream()
                .map(cartItemDto -> new OrderItemDto(
                        cartItemDto.getItemId(),
                        cartItemDto.getItemName(),
                        cartItemDto.getItemPrice(),  // itemPrice -> orderPrice 매핑
                        cartItemDto.getQuantity(),   // quantity -> count 매핑
                        cartItemDto.getImageUrl()
                ))
                .collect(Collectors.toList());

        return new OrderSaveRequestDto(
                cartCheckoutDto.getMemberId(),
                LocalDateTime.now(),
                orderItems,
                cartCheckoutDto.getCustomerName(),
                cartCheckoutDto.getCustomerPhone(),
                cartCheckoutDto.getCustomerAddress(),
                cartCheckoutDto.getShippingAddress(),
                cartCheckoutDto.getPaymentMethod(),
                cartCheckoutDto.getQuantity()
        );
    }
}
