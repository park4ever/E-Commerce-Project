package platform.ecommerce.dto.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import platform.ecommerce.entity.ItemCategory;

import java.util.List;

import static lombok.AccessLevel.*;

@Getter
@NoArgsConstructor(access = PROTECTED)
public class ItemSaveRequestDto {

    @NotNull(message = "상품명을 입력해주세요.")
    @Size(min = 1, max = 30, message = "상품명은 30자 이내여야 합니다.")
    private String itemName;

    @NotNull(message = "상품 설명을 입력해주세요.")
    @Size(min = 1, max = 100, message = "상품 설명은 100자 이내여야 합니다.")
    private String description;

    @NotNull(message = "카테고리를 선택해주세요.")
    private ItemCategory category;

    @NotNull(message = "상품 가격을 입력해주세요.")
    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private int price;

    @NotBlank(message = "브랜드명을 입력해주세요.")
    @Size(max = 20, message = "브랜드명은 20자 이내여야 합니다.")
    private String brand;

    private boolean isSelling = true;

    private MultipartFile image; //이미지 파일

    @Valid
    @NotEmpty(message = "최소 하나 이상의 옵션을 등록해주세요.")
    private List<ItemOptionDto> options;

    @Builder
    public ItemSaveRequestDto(String itemName, String description, ItemCategory category,
                              int price, String brand, MultipartFile image, List<ItemOptionDto> options) {
        this.itemName = itemName;
        this.description = description;
        this.category = category;
        this.price = price;
        this.brand = brand;
        this.image = image;
        this.options = options;
    }
}