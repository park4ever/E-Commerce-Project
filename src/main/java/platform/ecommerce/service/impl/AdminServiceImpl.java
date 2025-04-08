package platform.ecommerce.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import platform.ecommerce.dto.admin.*;
import platform.ecommerce.dto.item.ItemPageRequestDto;
import platform.ecommerce.dto.member.MemberPageRequestDto;
import platform.ecommerce.entity.*;
import platform.ecommerce.repository.*;
import platform.ecommerce.service.AdminService;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ReviewRepository reviewRepository;

    //회원 목록 조회(검색 및 정렬 포함)
    @Override
    @Transactional(readOnly = true)
    public Page<AdminMemberDto> getAllMembers(MemberPageRequestDto requestDto) {
        Pageable pageable = requestDto.toPageable();

        Page<Member> members = StringUtils.hasText(requestDto.getSearchKeyword())
                ? memberRepository.searchMembers(requestDto.getSearchKeyword(), requestDto.getSearchField(), pageable)
                : memberRepository.findAll(pageable);

        return members.map(this::convertToAdminMemberDto);
    }

    //회원 상세 조회
    @Override
    @Transactional(readOnly = true)
    public AdminMemberDto getMemberById(Long memberId) {
        return convertToAdminMemberDto(findEntityById(memberRepository, memberId, "회원"));
    }

    //회원 정보 수정
    @Override
    public void updateMember(Long memberId, AdminMemberDto updatedMemberDto) {
        Member member = findEntityById(memberRepository, memberId, "회원");

        member.updateMemberByAdmin(
                updatedMemberDto.getUsername(),
                updatedMemberDto.getPhoneNumber(),
                updatedMemberDto.getRole(),
                updatedMemberDto.isActive()
        );
    }

    //회원 활성화
    @Override
    public void activateMember(Long memberId) {
        Member member = findEntityById(memberRepository, memberId, "회원");
        member.activate();
        memberRepository.saveAndFlush(member);
    }

    //회원 삭제(비활성화)
    @Override
    public void deactivateMember(Long memberId) {
        Member member = findEntityById(memberRepository, memberId, "회원");
        member.deactivate();
        memberRepository.saveAndFlush(member); //명시적 저장
    }

    //상품 목록 조회(검색 및 정렬 포함)
    @Override
    @Transactional(readOnly = true)
    public Page<AdminItemDto> getAllItems(ItemPageRequestDto requestDto) {
        Pageable pageable = requestDto.toPageable();

        Page<Item> items = itemRepository.searchItems(requestDto, pageable);

        return items.map(this::convertToAdminItemDto);
    }

    //상품 상세 조회
    @Override
    @Transactional(readOnly = true)
    public AdminItemDto getItemById(Long itemId) {
        return convertToAdminItemDto(findEntityById(itemRepository, itemId, "상품"));
    }

    //상품 정보 수정
    @Override
    public void updateItem(Long itemId, AdminItemDto updatedItemDto) {
        Item item = findEntityById(itemRepository, itemId, "상품");

        item.updateItemDetails(
                updatedItemDto.getItemName(),
                updatedItemDto.getDescription(),
                updatedItemDto.getPrice(),
                updatedItemDto.getStockQuantity()
        );
    }

    //상품 활성/비활성화
    @Override
    public void toggleItemAvailability(Long itemId, boolean isAvailable) {
        Item item = findEntityById(itemRepository, itemId, "상품");

        if (isAvailable) {
            item.activate(); //활성화
        } else {
            item.deactivate(); //비활성화
        }

        itemRepository.save(item);
    }

    //상품 삭제
    @Override
    public void deleteItem(Long itemId) {
        itemRepository.delete(findEntityById(itemRepository, itemId, "상품"));
    }

    //주문 목록 조회(검색 및 정렬 포함)
    @Override
    @Transactional(readOnly = true)
    public Page<AdminOrderDto> getAllOrders(String searchKeyword, Pageable pageable) {
        Page<Order> orders = orderRepository.searchOrders(searchKeyword, pageable);

        return orders.map(this::convertToAdminOrderDto);
    }

    //주문 상세 조회
    @Override
    @Transactional(readOnly = true)
    public AdminOrderDto getOrderById(Long orderId) {
        return convertToAdminOrderDto(findEntityById(orderRepository, orderId, "주문"));
    }

    //주문 수정
    @Override
    public void updateOrder(Long orderId, AdminOrderUpdateRequest request) {
        Order order = findEntityById(orderRepository, orderId, "주문");

        //상태 변경
        if (request.getOrderStatus() != null) {
            order.updateStatus(request.getOrderStatus());
        }

        //배송지 변경
        if (request.getShippingAddress() != null && !request.getShippingAddress().isBlank()) {
            Address newAddress = Address.fromFullAddress(request.getShippingAddress().trim());
            order.updateShippingAddressByAdmin(newAddress);
        }

        //상태 변경 사유 입력
        if (request.getModificationReason() != null && !request.getModificationReason().isBlank()) {
            order.updateModificationReason(request.getModificationReason());
        }
    }

    //주문 취소
    @Override
    public void cancelOrder(Long orderId) {
        Order order = findEntityById(orderRepository, orderId, "주문");

        if (!order.isCancelable()) {
            throw new IllegalStateException("현재 상태에서는 주문을 취소할 수 없습니다.");
        }

        order.cancel();
    }

    //주문 상품 수량 변경
    @Override
    public void updateOrderItemQuantity(Long orderItemId, int newQuantity) {
        OrderItem orderItem = findEntityById(orderItemRepository, orderItemId, "주문 상품");

        if (newQuantity <= 0) {
            throw new IllegalArgumentException("주문 수량은 1 이상이어야 합니다.");
        }

        orderItem.updateQuantity(newQuantity);
    }

    //주문 상품 가격 변경
    @Override
    public void updateOrderItemPrice(Long orderItemId, int newPrice) {
        OrderItem orderItem = findEntityById(orderItemRepository, orderItemId, "주문 상품");

        if (newPrice < 0) {
            throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
        }

        if (orderItem.getOrderPrice() == newPrice) {
            throw new IllegalStateException("변경할 값이 현재 가격과 동일합니다.");
        }

        orderItem.updateOrderPrice(newPrice);
    }

    //주문 상품 삭제
    @Override
    public void deleteOrderItem(Long orderItemId) {
        OrderItem orderItem = findEntityById(orderItemRepository, orderItemId, "주문 상품");

        if (orderItem.getOrder().getOrderStatus() == OrderStatus.SHIPPED ||
            orderItem.getOrder().getOrderStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("배송된 상품은 삭제할 수 없습니다.");
        }

        orderItemRepository.delete(orderItem);
    }

    //리뷰 목록 조회(검색 및 정렬 포함)
    @Override
    @Transactional(readOnly = true)
    public Page<AdminReviewDto> getAllReviews(String searchKeyword, Pageable pageable) {
        Page<Review> reviews = reviewRepository.searchReviews(searchKeyword, pageable);

        return reviews.map(this::convertToAdminReviewDto);
    }

    //리뷰 상세 조회
    @Override
    @Transactional(readOnly = true)
    public AdminReviewDto getReviewById(Long reviewId) {
        return convertToAdminReviewDto(findEntityById(reviewRepository, reviewId, "리뷰"));
    }

    //리뷰 삭제
    @Override
    public void deleteReview(Long reviewId) {
        Review review = findEntityById(reviewRepository, reviewId, "리뷰");

        if (review == null) {
            throw new EntityNotFoundException("삭제할 리뷰가 존재하지 않습니다.");
        }

        reviewRepository.delete(review);
    }

    //리뷰 공개/비공개 설정
    @Override
    public void toggleReviewVisibility(Long reviewId, boolean isVisible) {
        Review review = findEntityById(reviewRepository, reviewId, "리뷰");

        if (review.isVisible() == isVisible) {
            throw new IllegalStateException("이미 해당 상태로 설정되어 있습니다.");
        }

        review.toggleVisibility(isVisible);
    }

    //관리자 답변 추가
    @Override
    public void addAdminReply(Long reviewId, String reply) {
        Review review = findEntityById(reviewRepository, reviewId, "리뷰");

        if (reply == null || reply.trim().isEmpty()) {
            throw new IllegalArgumentException("관리자 답변은 비어 있을 수 없습니다.");
        }

        review.addAdminReply(reply);
    }

    //관리자 답변 삭제
    @Override
    public void removeAdminReply(Long reviewId) {
        Review review = findEntityById(reviewRepository, reviewId, "리뷰");

        if (review.getAdminReply() == null) {
            throw new IllegalStateException("삭제할 관리자 답변이 존재하지 않습니다.");
        }

        review.removeAdminReply();
    }

    /**
     * 대시보드 통계용
     */
    @Override
    @Transactional(readOnly = true)
    public long countMembers() {
        return memberRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countNewMembers(int days) {
        LocalDateTime fromDate = LocalDateTime.now().minusDays(days);
        return memberRepository.countByCreatedDateAfter(fromDate);
    }

    @Override
    @Transactional(readOnly = true)
    public long countItems() {
        return itemRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countOrders() {
        return orderRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countRecentOrders(int days) {
        LocalDateTime fromDate = LocalDateTime.now().minusDays(days);
        return orderRepository.countByCreatedDateAfter(fromDate);
    }

    @Override
    @Transactional(readOnly = true)
    public long calculateTotalRevenue() {
        return orderRepository.calculateTotalRevenue();
    }

    @Override
    @Transactional(readOnly = true)
    public long calculateRecentRevenue(int days) {
        LocalDateTime fromDate = LocalDateTime.now().minusDays(days);
        return orderRepository.calculateRevenueSince(fromDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItem> getOrderItemsWithOrder(Long orderId) {
        return orderRepository.findOrderItemsWithOrder(orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countReviews() {
        return reviewRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countRecentReviews(int days) {
        LocalDateTime fromDate = LocalDateTime.now().minusDays(days);
        return reviewRepository.countByCreatedDateAfter(fromDate);
    }

    /**
     * 공통 메서드
     */

    //ID를 기반으로 엔티티 찾기(제네릭)
    private <T> T findEntityById(JpaRepository<T, Long> repository, Long id, String entityName) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityName + " ID [ " + id + " ]를 찾을 수 없습니다."));
    }

    //AdminMemberDto 변환
    private AdminMemberDto convertToAdminMemberDto(Member member) {
        return AdminMemberDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .username(member.getUsername())
                .phoneNumber(member.getPhoneNumber())
                .dateOfBirth(member.getDateOfBirth())
                .role(member.getRole())
                .createdDate(member.getCreatedDate())
                .lastModifiedDate(member.getLastModifiedDate())
                .isActive(member.getIsActive())
                .build();
    }

    //AdminItemDto 변환
    private AdminItemDto convertToAdminItemDto(Item item) {
        String imageUrl = item.getImageUrl();

        if (imageUrl == null || imageUrl.isBlank()) {
            imageUrl = "/item/default.png";
        } else {
            imageUrl = imageUrl.startsWith("/") ? imageUrl.substring(1) : imageUrl;
            imageUrl = "/images/" + imageUrl;
        }

        return AdminItemDto.builder()
                .id(item.getId())
                .itemName(item.getItemName())
                .description(item.getDescription())
                .price(item.getPrice())
                .stockQuantity(item.getStockQuantity())
                .imageUrl(imageUrl)
                .createdDate(item.getCreatedDate())
                .lastModifiedDate(item.getLastModifiedDate())
                .totalSales(itemRepository.getTotalSalesByItemId(item.getId()))
                .isAvailable(item.isAvailable())
                .build();
    }

    //AdminOrderDto 변환
    private AdminOrderDto convertToAdminOrderDto(Order order) {
        List<AdminOrderItemDto> orderItems = order.getOrderItems().stream()
                .map(orderItem -> new AdminOrderItemDto(
                        orderItem.getItem().getId(),
                        orderItem.getItem().getItemName(),
                        orderItem.getOrderPrice(),
                        orderItem.getCount(),
                        orderItem.getItem().getImageUrl()
                ))
                .collect(toList());

        Address address = order.getShippingAddress();

        return AdminOrderDto.builder()
                .id(order.getId())
                .memberEmail(order.getMember().getEmail())
                .memberName(order.getMember().getUsername())
                .orderDate(order.getOrderDate())
                .orderStatus(order.getOrderStatus())
                .paymentMethod(order.getPaymentMethod())
                .zipcode(address != null ? address.getZipcode() : "")
                .city(address != null ? address.getCity() : "")
                .street(address != null ? address.getStreet() : "")
                .additionalInfo(address != null ? address.getAdditionalInfo() : "")
                .lastModifiedDate(order.getLastModifiedDate())
                .modificationReason(order.getModificationReason())
                .orderItems(orderItems)
                .totalAmount(order.getTotalPrice())
                .isPaid(order.isPaid())
                .isCancelable(order.isCancelable())
                .build();
    }

    //AdminReviewDto 변환
    private AdminReviewDto convertToAdminReviewDto(Review review) {
        return AdminReviewDto.builder()
                .id(review.getId())
                .memberEmail(review.getMember().getEmail())
                .itemId(review.getItem().getId())
                .itemName(review.getItem().getItemName())
                .content(review.getContent())
                .rating(review.getRating())
                .imageUrl(review.getImageUrl())
                .createdDate(review.getCreatedDate())
                .lastModifiedDate(review.getLastModifiedDate())
                .isVisible(review.isVisible())
                .adminReply(review.getAdminReply())
                .build();
    }

    //회원 정렬 메서드
    private Sort getSortForMembers(String sortBy) {
        return "createdDate".equals(sortBy) ? Sort.by(ASC, "createdDate") : Sort.by(ASC, "lastModifiedDate");
    }

    //상품 정렬 메서드
    private Sort getSortForItem(String sortBy) {
        return switch (sortBy) {
            case "price" -> Sort.by(ASC, "price");
            case "priceDesc" -> Sort.by(DESC, "price");
            default -> Sort.by(DESC, "createdDate");
        };
    }
}