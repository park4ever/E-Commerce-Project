package platform.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartCheckoutDto {

    private Long memberId;
    private List<CartItemDto> cartItems;
}
