package platform.ecommerce.dto.admin;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminOrderItemDto {

    private Long orderItemId;
    private Long itemId;
    private String itemName;
    private int orderPrice;
    private int count;
    private String imageUrl;
}
