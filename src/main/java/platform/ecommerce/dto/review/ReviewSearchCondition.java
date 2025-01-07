package platform.ecommerce.dto.review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewSearchCondition {

    private Long itemId;        //상품 ID
    private String sortBy;      //정렬 기준

    public void updateItemId(Long itemId) {
        this.itemId = itemId;
    }
}
