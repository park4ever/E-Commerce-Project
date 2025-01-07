package platform.ecommerce.dto.item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import platform.ecommerce.entity.Category;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemSaveRequestDto {

    @NotNull(message = "상품명은 필수 입력 항목입니다.")
    @Size(min = 1, max = 30, message = "상품명은 1 ~ 30자로 입력하셔야 합니다.")
    private String itemName;

    @NotNull(message = "상품 설명은 필수 입력 항목입니다.")
    @Size(min = 1, max = 100, message = "상품 설명은 1 ~ 100자로 입력하셔야 합니다.")
    private String description;

    @NotNull(message = "상품 가격은 필수 입력 항목입니다.")
    @Min(value = 0, message = "가격은 0보다 커야 합니다.")
    private int price;

    @NotNull(message = "상품 재고 수량은 필수 입력 항목입니다.")
    @Min(value = 0, message = "상품 재고 수량은 0보다 커야 합니다.")
    private int stockQuantity;

    private MultipartFile image; //이미지 파일

//    private Category category = Category.NEW_ARRIVALS; //기본값 설정
}