package platform.ecommerce.dto.item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemOptionDto {

    private Long id;

    @NotBlank(message = "사이즈 지정은 필수입니다.")
    private String sizeLabel;

    @Min(value = 0, message = "재고 수량은 0 이상이어야 합니다.")
    private int stockQuantity;
}
