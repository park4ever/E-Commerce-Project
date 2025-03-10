package platform.ecommerce.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import platform.ecommerce.dto.admin.*;
import platform.ecommerce.entity.Item;
import platform.ecommerce.entity.Member;
import platform.ecommerce.entity.Order;
import platform.ecommerce.entity.OrderStatus;
import platform.ecommerce.repository.*;
import platform.ecommerce.service.AdminService;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.*;

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
    public List<AdminMemberDto> getAllMembers(String searchKeyword, String sortBy) {
        Sort sort = Sort.unsorted(); // 기본 정렬 없음
        if ("createdDate".equals(sortBy)) {
            sort = Sort.by(ASC, "createdDate");
        } else if ("lastModifiedDate".equals(sortBy)) {
            sort = Sort.by(ASC, "lastModifiedDate");
        }

        List<Member> members = StringUtils.hasText(searchKeyword)
                ? memberRepository.searchActiveMembers(searchKeyword, searchKeyword, sort)
                : memberRepository.findAllActiveMembers(sort);

        return members.stream()
                .map(member -> new AdminMemberDto(
                        member.getId(),
                        member.getEmail(),
                        member.getUsername(),
                        member.getPhoneNumber(),
                        member.getDateOfBirth(),
                        member.getRole(),
                        member.getCreatedDate(),
                        member.getLastModifiedDate()
                ))
                .collect(toList());
    }

    //회원 상세 조회
    @Override
    @Transactional(readOnly = true)
    public AdminMemberDto getMemberById(Long memberId) {
        Member member = memberRepository.findActiveMemberById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원 ID [" + memberId + "]를 찾을 수 없습니다."));

        return new AdminMemberDto(
                member.getId(),
                member.getEmail(),
                member.getUsername(),
                member.getPhoneNumber(),
                member.getDateOfBirth(),
                member.getRole(),
                member.getCreatedDate(),
                member.getLastModifiedDate()
        );
    }

    //회원 정보 수정
    @Override
    public void updateMember(Long memberId, AdminMemberDto updatedMemberDto) {
        Member member = memberRepository.findActiveMemberById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원 ID [" + memberId + "]를 찾을 수 없습니다."));

        member.updateMemberByAdmin(
                updatedMemberDto.getUsername(),
                updatedMemberDto.getPhoneNumber(),
                updatedMemberDto.getRole()
        );
    }

    //회원 삭제
    @Override
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findActiveMemberById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원 ID [" + memberId + "]를 찾을 수 없습니다."));

        member.deactivate();
        memberRepository.save(member); //명시적 저장
    }

    //상품 목록 조회(검색 및 정렬 포함)
    @Override
    @Transactional(readOnly = true)
    public List<AdminItemDto> getAllItems(String searchKeyword, String sortBy) {
        Sort sort = Sort.by(DESC, "createdDate"); // 기본 정렬: 최신순

        switch (sortBy) {
            case "price":
                sort = Sort.by(ASC, "price"); // 가격 낮은 순
                break;
            case "priceDesc":
                sort = Sort.by(DESC, "price"); // 가격 높은 순
                break;
            case "stockQuantity":
                sort = Sort.by(DESC, "stockQuantity"); // 재고 많은 순
                break;
            case "stockQuantityAsc":
                sort = Sort.by(ASC, "stockQuantity"); // 재고 적은 순
                break;
            case "createdDateAsc":
                sort = Sort.by(ASC, "createdDate"); // 오래된 순 (오래된 상품 먼저)
                break;
        }

        List<Item> items = itemRepository.searchItems(searchKeyword, sort);

        return items.stream()
                .map(item -> new AdminItemDto(
                        item.getId(),
                        item.getItemName(),
                        item.getDescription(),
                        item.getPrice(),
                        item.getStockQuantity(),
                        item.getImageUrl(),
                        item.getCreatedDate(),
                        item.getLastModifiedDate(),
                        itemRepository.getTotalSalesByItemId(item.getId()),
                        item.isAvailable()
                ))
                .collect(toList());
    }

    //상품 상세 조회
    @Override
    @Transactional(readOnly = true)
    public AdminItemDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("상품 ID [" + itemId + "]를 찾을 수 없습니다."));

        int totalSales = itemRepository.getTotalSalesByItemId(itemId);

        return new AdminItemDto(
                item.getId(),
                item.getItemName(),
                item.getDescription(),
                item.getPrice(),
                item.getStockQuantity(),
                item.getImageUrl(),
                item.getCreatedDate(),
                item.getLastModifiedDate(),
                totalSales,
                item.isAvailable()
        );
    }

    //상품 정보 수정
    @Override
    public void updateItem(Long itemId, AdminItemDto updatedItemDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("상품 ID [" + itemId + "]를 찾을 수 없습니다."));

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
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("상품 ID [" + itemId + "]를 찾을 수 없습니다."));

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
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("상품 ID [" + itemId + "]를 찾을 수 없습니다."));

        itemRepository.delete(item);
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
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문 ID [" + orderId + "]를 찾을 수 없습니다."));

        return convertToAdminOrderDto(order);
    }

    //주문 상태 변경
    @Override
    public void updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문 ID [" + orderId + "]를 찾을 수 없습니다."));

        order.updateStatus(newStatus);
    }

    @Override
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문 ID [" + orderId + "]를 찾을 수 없습니다."));

        if (!order.isCancelable()) {
            throw new IllegalStateException("현재 상태에서는 주문을 취소할 수 없습니다.");
        }

        order.cancel();
    }

    @Override
    public void updateOrderItem(Long orderItemId, int newQuantity) {

    }

    @Override
    public void deleteOrderItem(Long orderItemId) {

    }

    @Override
    public List<AdminReviewDto> getAllReviews(String searchKeyword, String sortBy) {
        return null;
    }

    @Override
    public AdminReviewDto getReviewById(Long reviewId) {
        return null;
    }

    @Override
    public void deleteReview(Long reviewId) {

    }

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

        return new AdminOrderDto(
                order.getId(),
                order.getMember().getEmail(),
                order.getMember().getUsername(),
                order.getOrderDate(),
                order.getOrderStatus(),
                order.getPaymentMethod(),
                order.getShippingAddress().toString(),
                order.getLastModifiedDate(),
                orderItems,
                order.getTotalPrice(),
                order.isPaid(),
                order.isCancelable()
        );
    }
}