package platform.ecommerce.dto.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemSearchCondition {

    private String itemName;
    private Integer minPrice;
    private Integer maxPrice;
    private String sortBy;
}
