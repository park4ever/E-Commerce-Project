package platform.ecommerce.dto.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import platform.ecommerce.entity.ItemCategory;

import java.util.List;

@Data
public class ItemResponseDto {

    private Long id;
    private String itemName;
    private String description;
    private Integer price;
    private String imageUrl;
    private Double averageRating;
    private List<ItemOptionDto> options;
    private ItemCategory category;

    @Builder
    public ItemResponseDto(Long id, String itemName, String description, Integer price,
                           String imageUrl, Double averageRating, List<ItemOptionDto> options,
                           ItemCategory category) {
        this.id = id;
        this.itemName = itemName;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.averageRating = averageRating;
        this.options = options;
        this.category = category;
    }
}
