package platform.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemUpdateDto {

    private String itemName;
    private String description;
    private Integer price;
    private Integer stockQuantity;

    public static ItemUpdateDto from(ItemResponseDto dto) {
        return ItemUpdateDto.builder()
                .itemName(dto.getItemName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stockQuantity(dto.getStockQuantity())
                .build();
    }
}
