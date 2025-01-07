package platform.ecommerce.dto.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class ReviewResponseDto {

    private Long reviewId;      //리뷰 ID
    private Long itemId;        //리뷰가 작성된 상품의 ID
    private Long memberId;      //리뷰를 작성한 회원의 ID
    private String memberName;  //리뷰를 작성한 회원의 이름
    private String content;     //리뷰 내용
    private Integer rating;     //별점
    private String imageUrl;
}
