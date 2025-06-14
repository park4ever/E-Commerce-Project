package platform.ecommerce.dto.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import platform.ecommerce.entity.DiscountType;

import java.time.LocalDateTime;

import static org.springframework.data.domain.Sort.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CouponPageRequestDto {

    private String searchField = "all";
    private String searchKeyword = "";
    private int page = 0;
    private int size = 10;
    private String sortBy = "createdDate";
    private Direction direction = Direction.DESC;

    private Boolean isEnabled;
    private DiscountType discountType;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;

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
                return direction;
            }
        }
        return direction;
    }

    public Pageable toPageable() {
        return PageRequest.of(page, size, Sort.by(getSortDirection(), getSortField()));
    }
}