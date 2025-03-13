package platform.ecommerce.controller.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import platform.ecommerce.service.AdminService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/order-items")
public class AdminOrderItemApiController {

    private final AdminService adminService;

    /**
     * 주문 상품 수량 변경
     */
    @PutMapping("/{id}/quantity")
    public ResponseEntity<Void> updateOrderItemQuantity(@PathVariable Long id,
                                                        @RequestParam int newQuantity) {
        adminService.updateOrderItemQuantity(id, newQuantity);

        return ResponseEntity.noContent().build();
    }

    /**
     * 주문 상품 가격 변경
     */
    @PutMapping("/{id}/price")
    public ResponseEntity<Void> updateOrderItemPrice(@PathVariable Long id,
                                                     @RequestParam int newPrice) {
        adminService.updateOrderItemPrice(id, newPrice);

        return ResponseEntity.noContent().build();
    }

    /**
     * 주문 상품 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long id) {
        adminService.deleteOrderItem(id);

        return ResponseEntity.noContent().build();
    }
}
