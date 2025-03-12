package platform.ecommerce.controller.api;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import platform.ecommerce.dto.admin.AdminOrderDto;
import platform.ecommerce.entity.OrderStatus;
import platform.ecommerce.service.AdminService;

import static org.springframework.data.domain.Sort.Direction.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderApiController {

    private final AdminService adminService;

    /**
     * 주문 목록 조회(검색 및 페이징 포함)
     */
    @GetMapping
    public ResponseEntity<Page<AdminOrderDto>> getAllOrders(
            @RequestParam(required = false) String searchKeyword,
            @PageableDefault(size = 10, sort = "orderDate", direction = DESC) Pageable pageable) {
        Page<AdminOrderDto> orders = adminService.getAllOrders(searchKeyword, pageable);

        return ResponseEntity.ok(orders);
    }

    /**
     * 주문 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<AdminOrderDto> getOrderById(@PathVariable Long id) {
        AdminOrderDto order = adminService.getOrderById(id);

        return ResponseEntity.ok(order);
    }

    /**
     * 주문 상태 변경
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable Long id,
                                                  @RequestBody OrderStatus newStatus) {
        adminService.updateOrderStatus(id, newStatus);

        return ResponseEntity.noContent().build();
    }

    /**
     * 주문 취소
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        adminService.cancelOrder(id);

        return ResponseEntity.noContent().build();
    }
}
