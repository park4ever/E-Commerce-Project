package platform.ecommerce.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import platform.ecommerce.dto.admin.*;
import platform.ecommerce.dto.item.ItemOptionDto;
import platform.ecommerce.dto.item.ItemPageRequestDto;
import platform.ecommerce.dto.member.MemberPageRequestDto;
import platform.ecommerce.dto.order.OrderPageRequestDto;
import platform.ecommerce.dto.review.ReviewPageRequestDto;
import platform.ecommerce.entity.*;
import platform.ecommerce.exception.item.InvalidItemOptionException;
import platform.ecommerce.exception.item.ItemPriceUnchangedException;
import platform.ecommerce.exception.order.OrderCannotBeCanceledException;
import platform.ecommerce.exception.order.OrderItemShippedException;
import platform.ecommerce.exception.order.OrderPriceInvalidException;
import platform.ecommerce.exception.order.OrderQuantityInvalidException;
import platform.ecommerce.exception.review.ReviewNotFoundException;
import platform.ecommerce.exception.review.ReviewReplyEmptyException;
import platform.ecommerce.exception.review.ReviewReplyNotFoundException;
import platform.ecommerce.exception.review.ReviewStatusAlreadySetException;
import platform.ecommerce.repository.*;
import platform.ecommerce.service.AdminService;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

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

        //기본 정보 업데이트
        item.updateItemDetails(
                updatedItemDto.getItemName(),
                updatedItemDto.getDescription(),
                updatedItemDto.getPrice(),
                updatedItemDto.getCategory()
        );

        //할인 가격 적용
        item.applyDiscountPrice(updatedItemDto.getDiscountPrice());

        //옵션 재고 업데이트
        if (updatedItemDto.getOptions() != null) {
            for (ItemOptionDto dto : updatedItemDto.getOptions()) {
                ItemOption option = item.getItemOptions().stream()
                        .filter(o -> o.getId().equals(dto.getId()))
                        .findFirst()
                        .orElseThrow(InvalidItemOptionException::new);
                option.updateStockQuantity(dto.getStockQuantity());
            }
        }
    }

    //상품 활성/비활성화
    @Override
    public void toggleItemAvailability(Long itemId, boolean isAvailable) {
        Item item = findEntityById(itemRepository, itemId, "상품");

        if (isAvailable) {
            item.makeAvailable(); //활성화
        } else {
            item.makeUnavailable(); //비활성화
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
    public Page<AdminOrderDto> getAllOrders(OrderPageRequestDto requestDto) {
        Pageable pageable = requestDto.toPageable();

        Page<Order> orders = orderRepository.searchOrders(requestDto, pageable);

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
            throw new OrderCannotBeCanceledException();
        }

        order.cancel();
    }

    //주문 상품 수량 변경
    @Override
    public void updateOrderItemQuantity(Long orderItemId, int newQuantity) {
        OrderItem orderItem = findEntityById(orderItemRepository, orderItemId, "주문 상품");

        if (newQuantity <= 0) {
            throw new OrderQuantityInvalidException();
        }

        orderItem.updateQuantity(newQuantity);
    }

    //주문 상품 가격 변경
    @Override
    public void updateOrderItemPrice(Long orderItemId, int newPrice) {
        OrderItem orderItem = findEntityById(orderItemRepository, orderItemId, "주문 상품");

        if (newPrice < 0) {
            throw new OrderPriceInvalidException();
        }

        if (orderItem.getOrderPrice() == newPrice) {
            throw new ItemPriceUnchangedException();
        }

        orderItem.updateOrderPrice(newPrice);
    }

    //주문 상품 삭제
    @Override
    public void deleteOrderItem(Long orderItemId) {
        OrderItem orderItem = findEntityById(orderItemRepository, orderItemId, "주문 상품");

        if (orderItem.getOrder().getOrderStatus() == OrderStatus.SHIPPED ||
            orderItem.getOrder().getOrderStatus() == OrderStatus.DELIVERED) {
            throw new OrderItemShippedException();
        }

        orderItem.cancel();

        Order order = orderItem.getOrder();
        order.getOrderItems().remove(orderItem);    //연관관계 해제
        orderItemRepository.delete(orderItem);      //명시적 삭제
    }

    //리뷰 목록 조회(검색 및 정렬 포함)
    @Override
    @Transactional(readOnly = true)
    public Page<AdminReviewDto> getAllReviews(ReviewPageRequestDto requestDto) {
        Pageable pageable = requestDto.toPageable();

        Page<Review> reviews = reviewRepository.searchReviews(requestDto, pageable);

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
            throw new ReviewNotFoundException();
        }

        reviewRepository.delete(review);
    }

    //리뷰 공개/비공개 설정
    @Override
    public void toggleReviewVisibility(Long reviewId, boolean isVisible) {
        Review review = findEntityById(reviewRepository, reviewId, "리뷰");

        if (review.isVisible() == isVisible) {
            throw new ReviewStatusAlreadySetException();
        }

        review.toggleVisibility(isVisible);
    }

    //관리자 답변 추가
    @Override
    public void addAdminReply(Long reviewId, String reply) {
        Review review = findEntityById(reviewRepository, reviewId, "리뷰");

        if (reply == null || reply.trim().isEmpty()) {
            throw new ReviewReplyEmptyException();
        }

        review.addAdminReply(reply);
    }

    //관리자 답변 삭제
    @Override
    public void removeAdminReply(Long reviewId) {
        Review review = findEntityById(reviewRepository, reviewId, "리뷰");

        if (review.getAdminReply() == null) {
            throw new ReviewReplyNotFoundException();
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

        int totalStock = item.getItemOptions().stream()
                .mapToInt(ItemOption::getStockQuantity)
                .sum();

        List<ItemOptionDto> optionDtos = item.getItemOptions().stream()
                .map(opt -> ItemOptionDto.builder()
                        .id(opt.getId())
                        .sizeLabel(opt.getSizeLabel())
                        .stockQuantity(opt.getStockQuantity())
                        .build())
                .toList();

        return AdminItemDto.builder()
                .id(item.getId())
                .itemName(item.getItemName())
                .description(item.getDescription())
                .price(item.getPrice())
                .stockQuantity(totalStock)
                .category(item.getCategory())
                .imageUrl(imageUrl)
                .createdDate(item.getCreatedDate())
                .lastModifiedDate(item.getLastModifiedDate())
                .totalSales(itemRepository.getTotalSalesByItemId(item.getId()))
                .isAvailable(item.isAvailable())
                .discountPrice(item.getDiscountPrice())
                .options(optionDtos)
                .build();
    }

    //AdminOrderDto 변환
    private AdminOrderDto convertToAdminOrderDto(Order order) {
        List<AdminOrderItemDto> orderItems = order.getOrderItems().stream()
                .map(orderItem -> {
                    ItemOption option = orderItem.getItemOption();
                    Item item = option.getItem();

                    return new AdminOrderItemDto(
                            orderItem.getId(),
                            item.getId(),
                            item.getItemName(),
                            orderItem.getOrderPrice(),
                            orderItem.getCount(),
                            item.getImageUrl()
                    );
                })
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
                .memberName(review.getMember().getUsername())
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
}