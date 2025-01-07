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

    private Long reviewId;      //리뷰 ID
    private Long itemId;        //리뷰를 작성할 상품의 ID
    private Long memberId;      //리뷰를 작성하는 회원의 ID
    private Long orderId;       //리뷰를 작성하는 주문의 ID
    private String content;     //리뷰 내용
    private Integer rating;     //별점(1 ~ 5점 사이)
    private MultipartFile image;

    public void updateItemId(Long itemId) {
        this.itemId = itemId;
    }

    public void updateOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
