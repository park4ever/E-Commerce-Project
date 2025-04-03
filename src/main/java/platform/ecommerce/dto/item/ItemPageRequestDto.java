package platform.ecommerce.dto.item;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import platform.ecommerce.entity.Item;

import static org.springframework.data.domain.Sort.*;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemPageRequestDto {

    private String searchKeyword = "";
    private String searchField = "all";
    private int page = 0;
    private int size = 10;
    private String sortBy = "createdDate";
    private Direction direction = DESC;

    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private Integer priceMin;
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private Integer priceMax;
    @Min(value = 0, message = "재고는 0 이상이어야 합니다.")
    private Integer stockMin;
    @Min(value = 0, message = "재고는 0 이상이어야 합니다.")
    private Integer stockMax;

    public String getSortField() {
        if (sortBy != null && sortBy.contains(",")) {
            return sortBy.split(",")[0];
        }
        return sortBy;
    }

    public Direction getSortDirection() {
        if (sortBy != null && sortBy.contains(",")) {
            try {
                return Direction.valueOf(sortBy.split(",")[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                return direction; //fallback
            }
        }
        return direction;
    }

    public Pageable toPageable() {
        return PageRequest.of(page, size, Sort.by(getSortDirection(), getSortField()));
    }
}
