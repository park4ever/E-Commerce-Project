package platform.ecommerce.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import platform.ecommerce.dto.admin.AdminItemDto;
import platform.ecommerce.dto.admin.AdminMemberDto;
import platform.ecommerce.dto.admin.AdminOrderDto;
import platform.ecommerce.dto.admin.AdminReviewDto;
import platform.ecommerce.entity.OrderStatus;

import java.util.List;

public interface AdminService {

    /**
     * 회원 관리
     */
    List<AdminMemberDto> getAllMembers(String searchKeyword, String sortBy);
    AdminMemberDto getMemberById(Long memberId);
    void updateMember(Long memberId, AdminMemberDto updatedMemberDto);
    void deleteMember(Long memberId);

    /**
     * 상품 관리
     */
    List<AdminItemDto> getAllItems(String searchKeyword, String sortBy);
    AdminItemDto getItemById(Long itemId);
    void updateItem(Long itemId, AdminItemDto updatedItemDto);
    void toggleItemAvailability(Long itemId, boolean isAvailable);
    void deleteItem(Long itemId);

    /**
     * 주문 관리
     */
    Page<AdminOrderDto> getAllOrders(String searchKeyword, Pageable pageable);
    AdminOrderDto getOrderById(Long orderId);
    void updateOrderStatus(Long orderId, OrderStatus newStatus);
    void cancelOrder(Long orderId);

    /**
     * 주문 상품 관리
     */
    void updateOrderItem(Long orderItemId, int newQuantity);
    void deleteOrderItem(Long orderItemId);

    /**
     * 리뷰 관리
     */
    List<AdminReviewDto> getAllReviews(String searchKeyword, String sortBy);
    AdminReviewDto getReviewById(Long reviewId);
    void deleteReview(Long reviewId);
}
