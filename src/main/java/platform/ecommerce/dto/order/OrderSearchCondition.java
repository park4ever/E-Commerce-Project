package platform.ecommerce.dto.order;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import platform.ecommerce.entity.OrderStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class OrderSearchCondition {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private OrderStatus status;
    private String sortBy; //예 : "date", "status"
    private boolean ascending; //오름차순, 내림차순 여부
}
