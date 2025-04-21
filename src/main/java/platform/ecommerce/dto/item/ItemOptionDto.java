package platform.ecommerce.dto.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemOptionDto {

    private Long id;
    private String sizeLabel;
    private int stockQuantity;
}
