package platform.ecommerce.dto.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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

    /* 검색용 필드 */
    private Integer priceMin;
    private Integer priceMax;
    private Integer stockMin;
    private Integer stockMax;

    public Pageable toPageable() {
        return PageRequest.of(page, size, Sort.by(direction, sortBy));
    }
}
