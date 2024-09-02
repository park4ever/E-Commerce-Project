package platform.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
public class ItemResponseDto {

    private Long id;
    private String itemName;
    private String description;
    private Integer price;
    private Integer stockQuantity;

    @Builder
    public ItemResponseDto(Long id, String itemName, String description, Integer price, Integer stockQuantity) {
        this.id = id;
        this.itemName = itemName;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }
}
