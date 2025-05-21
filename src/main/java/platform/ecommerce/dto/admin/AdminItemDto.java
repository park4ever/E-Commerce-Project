package platform.ecommerce.dto.admin;

import lombok.*;
import platform.ecommerce.dto.item.ItemOptionDto;
import platform.ecommerce.entity.ItemCategory;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminItemDto {

    private Long id;
    private String itemName;
    private String description;
    private int price;
    private int stockQuantity;
    private ItemCategory category;
    private String imageUrl;

    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    private int totalSales = 0;

    private Boolean isAvailable;

    private Integer discountPrice;
    private List<ItemOptionDto> options;
}
