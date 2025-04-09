package platform.ecommerce.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.springframework.data.domain.Sort.*;
import static org.springframework.data.domain.Sort.Direction.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderPageRequestDto {

    private String searchField = "all";
    private String searchKeyword = "";
    private int page = 0;
    private int size = 10;
    private String sortBy = "orderDate";
    private Direction direction = DESC;

    public String getSortField() {
        return sortBy.contains(",") ? sortBy.split(",")[0] : sortBy;
    }

    public Direction getSortDirection() {
        try {
            return sortBy.contains(",") ? Direction.valueOf(sortBy.split(",")[1].toUpperCase()) : direction;
        } catch (IllegalArgumentException e) {
            return direction;
        }
    }

    public Pageable toPageable() {
        return PageRequest.of(page, size, Sort.by(getSortDirection(), getSortField()));
    }
}
