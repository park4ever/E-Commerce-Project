package platform.ecommerce.dto.item;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import platform.ecommerce.dto.member.MemberDetailsDto;
import platform.ecommerce.dto.review.ReviewResponseDto;

@Getter
@Builder
public class ItemDetailViewModel {

    private ItemResponseDto item;
    private MemberDetailsDto member;
    private Page<ReviewResponseDto> reviews;
    private double ratingAverage;
    private long reviewCount;
    private int defaultQuantity;

    public static ItemDetailViewModel of(ItemResponseDto item,
                                         MemberDetailsDto member,
                                         Page<ReviewResponseDto> reviews,
                                         double avgRating,
                                         long reviewCount) {
        return ItemDetailViewModel.builder()
                .item(item)
                .member(member != null ? member : MemberDetailsDto.guest())
                .reviews(reviews)
                .ratingAverage(avgRating)
                .reviewCount(reviewCount)
                .defaultQuantity(1)
                .build();
    }
}
