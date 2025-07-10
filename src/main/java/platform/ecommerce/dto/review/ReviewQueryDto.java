package platform.ecommerce.dto.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.springframework.data.domain.Sort.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewQueryDto {

    private Long itemId;

    private int page = 0;
    private int size = 10;

    private String sortBy = "createdDate";
    private Direction direction = Direction.DESC;

    public String getSortField() {
        return sortBy;
    }

    public Direction getSortDirection() {
        return direction;
    }

    public Pageable toPageable() {
        return PageRequest.of(page, size, Sort.by(direction, sortBy));
    }
}
