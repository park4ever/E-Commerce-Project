package platform.ecommerce.dto.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDto {

    private Long reviewId;
    private Long itemOptionId;
    private Long memberId;
    private Long orderId;
    private String content;
    private Integer rating;
    private MultipartFile image;

    public void updateItemId(Long itemId) {
        this.itemOptionId = itemId;
    }

    public void updateOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
