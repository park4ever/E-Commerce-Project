package platform.ecommerce.dto.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import platform.ecommerce.entity.ItemCategory;

import java.util.List;

import static lombok.AccessLevel.*;

@Getter
@NoArgsConstructor(access = PROTECTED)
public class ItemUpdateDto {

    @Size(min = 1, max = 30, message = "상품명은 30자 이내여야 합니다.")
    private String itemName;

    @Size(min = 1, max = 100, message = "상품 설명은 100자 이내여야 합니다.")
    private String description;

    @NotNull(message = "카테고리를 선택해주세요.")
    private ItemCategory category;

    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private Integer price;

    @Min(value = 0, message = "할인 가격은 0원 이상이어야 합니다.")
    private Integer discountPrice;

    @Size(max = 20, message = "브랜드명은 20자 이내여야 합니다.")
    private String brand;

    private Boolean isSelling;

    private String imageUrl; //기존 이미지 경로(프론트가 갖고 있는 상태)

    private MultipartFile image; //새 이미지가 업로드될 경우

    @Valid
    @NotEmpty(message = "최소 하나 이상의 옵션을 등록해주세요.")
    private List<ItemOptionDto> options;

    @Builder
    public ItemUpdateDto(String itemName, String description, ItemCategory category, Integer price,
                         Integer discountPrice, String brand, Boolean isSelling, String imageUrl,
                         MultipartFile image, List<ItemOptionDto> options) {
        this.itemName = itemName;
        this.description = description;
        this.category = category;
        this.price = price;
        this.discountPrice = discountPrice;
        this.brand = brand;
        this.isSelling = isSelling;
        this.imageUrl = imageUrl;
        this.image = image;
        this.options = options;
    }
}
