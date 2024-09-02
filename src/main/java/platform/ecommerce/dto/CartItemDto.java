package platform.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDto {

    private Long itemId;
    private String itemName;
    private Integer itemPrice; // 장바구니에 담긴 상품의 가격
    private Integer quantity;  // 장바구니에 담긴 수량

    public int getTotalPrice() {
        return itemPrice * quantity;
    }

    public boolean isValid() {
        return itemId != null && quantity > 0;
    }
}
