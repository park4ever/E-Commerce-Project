package platform.ecommerce.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import platform.ecommerce.entity.Address;
import platform.ecommerce.entity.PaymentMethod;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class CartCheckoutDto {

    private Long memberId;
    private List<CartItemDto> cartItems = new ArrayList<>();

    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private Address shippingAddress;
    private PaymentMethod paymentMethod;

    private Integer quantity;

    //장바구니가 비었을때
    public CartCheckoutDto(Long memberId, List<CartItemDto> cartItems) {
        this.memberId = memberId;
        this.cartItems = cartItems;
    }

    public CartCheckoutDto(Long memberId, List<CartItemDto> cartItems, String customerName,
                           String customerPhone, String customerAddress, Address shippingAddress,
                           PaymentMethod paymentMethod, Integer quantity) {
        this.memberId = memberId;
        this.cartItems = cartItems;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerAddress = customerAddress;
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
        this.quantity = quantity;
    }
}
