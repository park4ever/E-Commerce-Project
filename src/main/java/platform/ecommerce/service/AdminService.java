package platform.ecommerce.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import platform.ecommerce.dto.admin.AdminItemDto;
import platform.ecommerce.dto.admin.AdminMemberDto;
import platform.ecommerce.dto.admin.AdminOrderDto;
import platform.ecommerce.dto.admin.AdminReviewDto;
import platform.ecommerce.entity.OrderItem;
import platform.ecommerce.entity.OrderStatus;

import java.util.List;

public interface AdminService {

    /**
     * 회원 관리
     */
    Page<AdminMemberDto> getAllMembers(String searchKeyword, Pageable pageable);
    AdminMemberDto getMemberById(Long memberId);
    void updateMember(Long memberId, AdminMemberDto updatedMemberDto);
    void activateMember(Long memberId);
    void deactivateMember(Long memberId);

    /**
     * 상품 관리
     */
    Page<AdminItemDto> getAllItems(String searchKeyword, Pageable pageable);
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
    void updateOrderItemQuantity(Long orderItemId, int newQuantity);
    void updateOrderItemPrice(Long orderItemId, int newPrice);
    void deleteOrderItem(Long orderItemId);

    /**
     * 리뷰 관리
     */
    Page<AdminReviewDto> getAllReviews(String searchKeyword, Pageable pageable);
    AdminReviewDto getReviewById(Long reviewId);
    void deleteReview(Long reviewId);
    void toggleReviewVisibility(Long reviewId, boolean isVisible);
    void addAdminReply(Long reviewId, String reply);
    void removeAdminReply(Long reviewId);

    /**
     * 대쉬보드 통계용
     */
    long countMembers();
    long countNewMembers(int days);
    long countItems();
    long countOrders();
    long countRecentOrders(int days);
    long calculateTotalRevenue();
    long calculateRecentRevenue(int days);
    List<OrderItem> getOrderItemsWithOrder(Long orderId);
    long countReviews();
    long countRecentReviews(int days);
}
