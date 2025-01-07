package platform.ecommerce.dto.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import platform.ecommerce.entity.Category;

@Data
public class ItemResponseDto {

    private Long id;
    private String itemName;
    private String description;
    private Integer price;
    private Integer stockQuantity;
    private String imageUrl;

    private Double averageRating; //평균 별점 추가

//    private Category category;

    @Builder
    public ItemResponseDto(Long id, String itemName, String description, Integer price, Integer stockQuantity, String imageUrl) {
        this.id = id;
        this.itemName = itemName;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.imageUrl = imageUrl;
//        this.category = category;
    }
}
