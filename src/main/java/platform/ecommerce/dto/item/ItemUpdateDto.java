package platform.ecommerce.dto.item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import platform.ecommerce.entity.ItemCategory;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemUpdateDto {

    @Size(min = 1, max = 30, message = "상품명은 1 ~ 30자로 입력하셔야 합니다.")
    private String itemName;

    @Size(min = 1, max = 100, message = "상품 설명은 1 ~ 100자로 입력하셔야 합니다.")
    private String description;

    @Min(value = 0, message = "가격은 0보다 커야 합니다.")
    private Integer price;

    private Integer discountPrice;

    private String imageUrl; //이미지 URL

    private MultipartFile image; //업로드할 새 이미지 파일

    private List<ItemOptionDto> options;

    private ItemCategory category;
}
