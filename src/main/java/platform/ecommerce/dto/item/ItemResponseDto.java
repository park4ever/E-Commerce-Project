package platform.ecommerce.dto.item;

import lombok.*;
import platform.ecommerce.entity.ItemCategory;

import java.util.List;

import static lombok.AccessLevel.*;

@Getter
@NoArgsConstructor(access = PROTECTED)
public class ItemResponseDto {

    private Long id;
    private String itemName;
    private String description;
    private Integer price;
    private Integer discountPrice;
    private String brand;
    private Boolean isSelling;
    private Long viewCount;

    private String imageUrl;
    private Double averageRating;

    private List<ItemOptionDto> options;
    private ItemCategory category;

    @Builder
    public ItemResponseDto(Long id, String itemName, String description, Integer price, Integer discountPrice,
                           String brand, Boolean isSelling, Long viewCount, String imageUrl, Double averageRating,
                           List<ItemOptionDto> options, ItemCategory category) {
        this.id = id;
        this.itemName = itemName;
        this.description = description;
        this.price = price;
        this.discountPrice = discountPrice;
        this.brand = brand;
        this.isSelling = isSelling;
        this.viewCount = viewCount;
        this.imageUrl = imageUrl;
        this.averageRating = averageRating;
        this.options = options;
        this.category = category;
    }
}
