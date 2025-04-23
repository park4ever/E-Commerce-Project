package platform.ecommerce.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDto {

    private Long itemOptionId;
    private String itemName;
    private String sizeLabel;
    private Integer itemPrice;      // 장바구니에 담긴 상품의 가격
    private Integer quantity;       // 장바구니에 담긴 수량
    private String imageUrl;

    public int getTotalPrice() {
        return itemPrice * quantity;
    }

    public boolean isValid() {
        return itemOptionId != null && quantity > 0;
    }
}
