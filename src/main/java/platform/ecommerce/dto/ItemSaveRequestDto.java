package platform.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemSaveRequestDto {

    private String itemName;
    private String description;
    private int price;
    private int stockQuantity;
}
