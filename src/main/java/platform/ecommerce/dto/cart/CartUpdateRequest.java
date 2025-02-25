package platform.ecommerce.dto.cart;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartUpdateRequest {

    @JsonProperty("itemId")
    private Long itemId;

    @JsonProperty("quantity")
    private Integer quantity;
}
