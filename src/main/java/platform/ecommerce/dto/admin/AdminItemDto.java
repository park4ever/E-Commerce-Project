package platform.ecommerce.dto.admin;

import lombok.*;

import java.time.LocalDateTime;

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
    private String imageUrl;

    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    private int totalSales = 0;

    private Boolean isAvailable;
}
